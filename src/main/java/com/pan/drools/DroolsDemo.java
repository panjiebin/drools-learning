package com.pan.drools;

import com.pan.drools.pojo.Applicant;

/**
 * @author panjb
 */
public class DroolsDemo {

    public static void main(String[] args) {
        DroolsManager droolsManager = new DroolsManager();
        String ruleContent = "package com.company.license\n" +
//                "import com.pan.drools.pojo.Applicant\n" +
                "\n" +
                "rule \"r-1\"\n" +
                "when\n" +
                "  $a : Applicant(age < 18)\n" +
                "then\n" +
                "  $a.setValid(false);\n" +
                "end";
        DroolsRule rule = new DroolsRule();
        rule.setId("000001");
        rule.setContent(ruleContent);
        rule.setPackageName("com.company.license");
        rule.setKieBaseName("default");
        droolsManager.addOrUpdateRule(rule);

        Applicant applicant = new Applicant("zs", 16);
        droolsManager.exec("default", applicant);
        System.out.println("applicant = " + applicant.isValid());

        Applicant applicant2 = new Applicant("li", 16);
        String ruleContent2 = "package com.company.license\n" +
                "import com.pan.drools.pojo.Applicant\n" +
                "\n" +
                "rule \"r-1\"\n" +
                "when\n" +
                "  $a : Applicant(age < 18)\n" +
                "then\n" +
                "  $a.setValid(false);\n" +
                "end";
        rule.setId("r-2");
        rule.setContent(ruleContent2);
        droolsManager.addOrUpdateRule(rule);
        droolsManager.exec("default", applicant2);
        System.out.println("ruleContent2 = " + applicant2.isValid());
        droolsManager.deleteRule(rule);
        Applicant applicant3 = new Applicant("zs", 19);
        droolsManager.exec("default", applicant2);
        System.out.println("applicant3.isValid() = " + applicant3.isValid());
    }
}
