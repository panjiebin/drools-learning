package com.pan.drools;

import com.pan.drools.pojo.Applicant;
import org.drools.compiler.kie.builder.impl.InternalKieModule;
import org.drools.compiler.kie.builder.impl.KieContainerImpl;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.Message;
import org.kie.api.builder.Results;
import org.kie.api.builder.model.KieBaseModel;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.conf.SessionsPoolOption;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieContainerSessionsPool;
import org.kie.api.runtime.StatelessKieSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * @author panjb
 */
public class DroolsManager {

    private final static Logger logger = LoggerFactory.getLogger(DroolsManager.class);

    public static final String KMODULE_SRC_RESOURCES_PATH = "src/main/resources/";
    public static final String DEFAULT_KIE_BASE_NAME = "default";
    private static final int DEFAULT_POOL_SIZE = 10;
    private static final String KIE_BASE_SESSION = "-session";

    private final KieServices kieServices;
    private final KieModuleModel kieModuleModel;
    private final KieFileSystem kfs;
    private final KieContainer kieContainer;
    private final int poolSize;
    private final KieContainerSessionsPool pool;

    public DroolsManager() {
        this(DEFAULT_POOL_SIZE);
    }

    public DroolsManager(int poolSize) {
        this.poolSize = poolSize;
        // KieServices是一个单例，可以访问 Kie 所有构建和运行时组件
        this.kieServices = KieServices.Factory.get();
        // 创建 KieModuleModel，用于定义 KieBases 和 KieSessions，用于通过 API 定义 kmodule.xml
        kieModuleModel = kieServices.newKieModuleModel();
        // 创建 KieFileSystem（一种虚拟文件系统，基于内存），外部资源（drl文件）写入到 KieFileSystem
        kfs = kieServices.newKieFileSystem();
        // crate default kieBase
        this.createKieBase(DEFAULT_KIE_BASE_NAME, true);
        // 构建 KieFileSystem 的所有内容
        kieServices.newKieBuilder(kfs).buildAll();
        // 创建 KieContainer，通过 KieContainer 获取 KieBases 并创建新的 KieSessions
        kieContainer = kieServices.newKieContainer(kieServices.getRepository().getDefaultReleaseId());
        pool = kieContainer.newKieSessionsPool(this.poolSize);
    }

    public void createKieBase(String kieBaseName, boolean isDefault) {
        if (this.existsKieBase(kieBaseName)) {
            return;
        }
        KieBaseModel kieBaseModel = kieModuleModel.newKieBaseModel(kieBaseName)
                .setDefault(isDefault)
                .setSessionsPool(SessionsPoolOption.get(this.poolSize));
        kieBaseModel.newKieSessionModel(kieBaseName + KIE_BASE_SESSION)
                .setDefault(isDefault);
        // 将 KieModuleModel 转成 kmodule.xml
        kfs.writeKModuleXML(kieModuleModel.toXML());
        if (logger.isInfoEnabled()) {
            logger.info("Created kieBase [{}]", kieBaseName);
        }
    }

    public boolean existsKieBase(String kieBaseName) {
        if (kieContainer == null) {
            return false;
        }
        Collection<String> kieBaseNames = kieContainer.getKieBaseNames();
        return kieBaseNames.contains(kieBaseName);
    }

    public void deleteRule(DroolsRule rule) {
        this.deleteRules(Collections.singletonList(rule));
    }

    public void deleteRules(DroolsRule... rules) {
        this.deleteRules(Arrays.asList(rules));
    }

    public void deleteRules(Collection<DroolsRule> rules) {
        for (DroolsRule rule : rules) {
            if (existsKieBase(rule.getKieBaseName())) {
                KieBase kieBase = this.kieContainer.getKieBase(rule.getKieBaseName());
                // TODO ruleName
                kieBase.removeRule(rule.getPackageName(), rule.getId());
                if (logger.isInfoEnabled()) {
                    logger.info("Deleted rule [{}] [{}]", rule.getId(), rule.getKieBaseName());
                }
            }
        }
    }

    public void addOrUpdateRule(DroolsRule rule) {
        this.addOrUpdateRules(Collections.singletonList(rule));
    }

    public void addOrUpdateRules(DroolsRule... rules) {
        this.addOrUpdateRules(Arrays.asList(rules));
    }

    public void addOrUpdateRules(Collection<DroolsRule> rules) {
        for (DroolsRule rule : rules) {
            if (!existsKieBase(rule.getKieBaseName())) {
                this.createKieBase(rule.getKieBaseName(), false);
            }
            KieBaseModel kieBaseModel = kieModuleModel.getKieBaseModels().get(rule.getKieBaseName());
            List<String> packages = kieBaseModel.getPackages();
            if (!packages.contains(rule.getPackageName())) {
                kieBaseModel.addPackage(rule.getPackageName());
            }
            this.writeDrl(rule);
        }
        KieBuilder kieBuilder = kieServices.newKieBuilder(kfs);
        kieBuilder.buildAll();
        Results results = kieBuilder.getResults();
        List<Message> messages = results.getMessages(Message.Level.ERROR);
        if (messages != null && !messages.isEmpty()) {
            Set<String> errorDrlPath = new HashSet<>();
            for (Message message : messages) {
                if (logger.isErrorEnabled()) {
                    logger.error("Error while building rule [{}]", message);
                }
                errorDrlPath.add(KMODULE_SRC_RESOURCES_PATH + message.getPath());
            }
            this.deleteDrlFile(errorDrlPath.toArray(new String[0]));
        }
        else {
            ((KieContainerImpl) kieContainer).updateToKieModule((InternalKieModule) kieBuilder.getKieModule());
        }
    }

    private void deleteDrlFile(String... paths) {
        kfs.delete(paths);
        if (logger.isInfoEnabled()) {
            logger.info("Deleted DRL file [{}]", String.join(",", paths));
        }
    }

    private void writeDrl(DroolsRule rule) {
        String filePath = KMODULE_SRC_RESOURCES_PATH + rule.getKieBaseName() + "/" + rule.getId() + ".drl";
        kfs.write(filePath, rule.getContent());
    }

    public boolean exec(String kieBaseName, Applicant applicant) {
        StatelessKieSession kieSession = pool.newStatelessKieSession(kieBaseName + KIE_BASE_SESSION);
        kieSession.execute(applicant);
        return applicant.isValid();
    }
}
