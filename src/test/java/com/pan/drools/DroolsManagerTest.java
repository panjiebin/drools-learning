package com.pan.drools;

import com.pan.drools.pojo.Applicant;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.kie.api.command.BatchExecutionCommand;
import org.kie.api.event.rule.AgendaEventListener;
import org.kie.api.runtime.StatelessKieSession;

import static org.junit.Assert.*;

public class DroolsManagerTest {
    private final DroolsManager droolsManager = new DroolsManager();

    @Test
    public void testAdd() {
        DroolsRule droolsRule = new DroolsRule();
        droolsRule.setId("1");
        String content = "package com.pan.rules.test\n" +
                "import com.pan.drools.pojo.Applicant\n" +
                "\n" +
                "rule \"r0001\"\n" +
                "    when\n" +
                "        $a: Applicant(age < 18)\n" +
                "    then\n" +
                "        $a.setValid(false);\n" +
                "end";
        droolsRule.setContent(content);
        droolsRule.setKieBaseName("defaultKieBase");
        droolsRule.setPackageName("com.pan.rules.test");

        droolsManager.addOrUpdateRule(droolsRule);
        Applicant applicant = new Applicant();
        applicant.setAge(16);
        applicant.setName("Jack");
        StatelessKieSession kieSession = droolsManager.getStatelessKieSession("defaultKieBase");
        kieSession.execute(applicant);
        Assert.assertFalse(applicant.isValid());
    }

    @Test
    public void testDelete() {
        DroolsRule droolsRule = new DroolsRule();
        droolsRule.setId("1");
        String content = "package com.pan.rules.test\n" +
                "import com.pan.drools.pojo.Applicant\n" +
                "\n" +
                "rule \"r0001\"\n" +
                "    when\n" +
                "        $a: Applicant(age < 18)\n" +
                "    then\n" +
                "        $a.setValid(false);\n" +
                "end";
        droolsRule.setContent(content);
        droolsRule.setKieBaseName("defaultKieBase");
        droolsRule.setPackageName("com.pan.rules.test");

        droolsManager.addOrUpdateRule(droolsRule);
        Applicant applicant = new Applicant();
        applicant.setAge(16);
        applicant.setName("Jack");
        StatelessKieSession kieSession = droolsManager.getStatelessKieSession("defaultKieBase");
        kieSession.execute(applicant);
        Assert.assertFalse(applicant.isValid());
        droolsManager.deleteRules(droolsRule);
        Applicant applicant2 = new Applicant();
        applicant2.setAge(16);
        applicant2.setName("Lisa");
        kieSession.execute(applicant2);
        Assert.assertTrue(applicant2.isValid());
    }

    @Test
    public void testUpdate() {
        DroolsRule droolsRule = new DroolsRule();
        droolsRule.setId("1");
        String content = "package com.pan.rules.test\n" +
                "import com.pan.drools.pojo.Applicant\n" +
                "\n" +
                "rule \"r0001\"\n" +
                "    when\n" +
                "        $a: Applicant(age < 18)\n" +
                "    then\n" +
                "        $a.setValid(false);\n" +
                "end";
        droolsRule.setContent(content);
        droolsRule.setKieBaseName("defaultKieBase");
        droolsRule.setPackageName("com.pan.rules.test");

        droolsManager.addOrUpdateRule(droolsRule);
        Applicant applicant = new Applicant();
        applicant.setAge(16);
        applicant.setName("Jack");
        StatelessKieSession kieSession = droolsManager.getStatelessKieSession("defaultKieBase");
        kieSession.execute(applicant);
        Assert.assertFalse(applicant.isValid());
        String content2 = "package com.pan.rules.test\n" +
                "import com.pan.drools.pojo.Applicant\n" +
                "\n" +
                "rule \"r0001\"\n" +
                "    when\n" +
                "        $a: Applicant(age < 18 && name == \"zhangsan\")\n" +
                "    then\n" +
                "        $a.setValid(false);\n" +
                "end";
        droolsRule.setContent(content2);
        droolsManager.addOrUpdateRule(droolsRule);
        applicant.setValid(true);
        kieSession.execute(applicant);
        Assert.assertTrue(applicant.isValid());
    }

    @Test
    public void testAgendaGroup() {
        DroolsRule droolsRule = new DroolsRule();
        droolsRule.setId("1");
        String content = "package com.pan.rules.test\n" +
                "import com.pan.drools.pojo.Applicant\n" +
                "\n" +
                "rule \"r0001\"\n" +
                "    agenda-group \"g1\"\n" +
                "    when\n" +
                "        $a: Applicant(age < 18)\n" +
                "    then\n" +
                "        $a.setValid(false);\n" +
                "end\n" +
                "\n" +
                "rule \"r0002\"\n" +
                "    agenda-group \"g2\"\n" +
                "    when\n" +
                "        $a: Applicant(age < 18)\n" +
                "    then\n" +
                "        $a.setName(\"Lisa\");\n" +
                "end";
        droolsRule.setContent(content);
        droolsRule.setKieBaseName("defaultKieBase");
        droolsRule.setPackageName("com.pan.rules.test");
        droolsManager.addOrUpdateRule(droolsRule);
        Applicant applicant = new Applicant();
        applicant.setAge(16);
        applicant.setName("Jack");
        // StatelessKieSession 需要通过command 才能获取到指定的 agenda-group
        StatelessKieSession kieSession = droolsManager.getStatelessKieSession("defaultKieBase");
        BatchExecutionCommand command = droolsManager.getBatchExecutionCommand("g1", applicant);
        kieSession.execute(command);
        Assert.assertFalse(applicant.isValid());
        Assert.assertEquals("Jack", applicant.getName());

        BatchExecutionCommand command2 = droolsManager.getBatchExecutionCommand("g2", applicant);
        kieSession.execute(command2);
        Assert.assertEquals("Lisa", applicant.getName());
    }

    @Test
    public void testListener() {
        DroolsRule droolsRule = new DroolsRule();
        droolsRule.setId("1");
        String content = "package com.pan.rules.test\n" +
                "import com.pan.drools.pojo.Applicant\n" +
                "\n" +
                "rule \"r0001\"\n" +
                "    when\n" +
                "        $a: Applicant(age < 18)\n" +
                "    then\n" +
                "        $a.setValid(false);\n" +
                "end";
        droolsRule.setContent(content);
        droolsRule.setKieBaseName("defaultKieBase");
        droolsRule.setPackageName("com.pan.rules.test");

        droolsManager.addOrUpdateRule(droolsRule);
        Applicant applicant = new Applicant();
        applicant.setAge(16);
        applicant.setName("Jack");
        MatchedRuleListener listener = new MatchedRuleListener();
        StatelessKieSession kieSession = droolsManager.getStatelessKieSession("defaultKieBase", listener);
        kieSession.execute(applicant);
        Assert.assertEquals("r0001", listener.matchedRules.get(0));
    }

    @After
    public void tearDown() throws Exception {
        droolsManager.close();
    }
}