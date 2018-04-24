package ngeneanalysys.util;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.controlsfx.control.PopOver;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Jang
 * @since 2018-04-20
 */
public class PopOverUtil {

    private PopOverUtil() { throw new IllegalAccessError("PopOverUtil class"); }

    @SuppressWarnings("unchecked")
    public static void openACMGPopOver(Label label, Map<String, Object> acmg) {

        PopOver popOver = new PopOver();
        popOver.setArrowLocation(PopOver.ArrowLocation.BOTTOM_CENTER);
        popOver.setHeaderAlwaysVisible(true);
        popOver.setAutoHide(true);
        popOver.setAutoFix(true);
        popOver.setDetachable(true);
        popOver.setArrowSize(15);
        popOver.setMaxSize(460, 150);
        popOver.setPrefWidth(460);
        popOver.setMinSize(460, 150);
        popOver.setTitle("List of evidence in 28 criteria");

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setMaxSize(460, 200);

        VBox box = new VBox();
        box.setMaxWidth(445);
        box.getStyleClass().add("acmg_content_box");

        scrollPane.setContent(box);

        String[] results = acmg.containsKey("rules") ? ((String)acmg.get("rules")).split(",") : null;
        String rulesText = acmg.containsKey("rules") ? "(" + acmg.get("rules") + ")" : null;

        Label reason = new Label();
        String pathogenicity = acmg.containsKey("pathogenicity") ? (String)acmg.get("pathogenicity") : null;
        reason.setText(pathogenicity + rulesText);
        if("benign".equalsIgnoreCase(pathogenicity)) {
            reason.getStyleClass().add("benign");
        } else if("likely benign".equalsIgnoreCase(pathogenicity)) {
            reason.getStyleClass().add("likely_benign");
        } else if("uncertain significance".equalsIgnoreCase(pathogenicity)) {
            reason.getStyleClass().add("uncertain_significance");
        } else if("likely pathogenic".equalsIgnoreCase(pathogenicity)) {
            reason.getStyleClass().add("likely_pathogenic");
        } else if("pathogenic".equalsIgnoreCase(pathogenicity)) {
            reason.getStyleClass().add("pathogenic");
        }
        box.getChildren().add(reason);

        assert results != null;
        for(String result : results) {
            Map<String, Object> role = (Map<String, Object>) acmg.get(result);

            Label roleLabel = new Label(result);
            roleLabel.setMaxWidth(50);
            roleLabel.setWrapText(true);
            roleLabel.getStyleClass().add("acmg_content_role_label");
            if(result.startsWith("PVS")) {
                roleLabel.getStyleClass().add("acmg_PVS");
            } else if(result.startsWith("PS")) {
                roleLabel.getStyleClass().add("acmg_PS");
            } else if(result.startsWith("PM")) {
                roleLabel.getStyleClass().add("acmg_PM");
            } else if(result.startsWith("PP")) {
                roleLabel.getStyleClass().add("acmg_PP");
            } else if(result.startsWith("BP")) {
                roleLabel.getStyleClass().add("acmg_BP");
            } else if(result.startsWith("BS")) {
                roleLabel.getStyleClass().add("acmg_BS");
            } else if(result.startsWith("BA")) {
                roleLabel.getStyleClass().add("acmg_BA");
            }
            box.getChildren().add(roleLabel);

            Label descLabel = new Label();
            String desc = role.containsKey("desc") ? (String)role.get("desc") : null;
            descLabel.setWrapText(true);
            descLabel.setText(desc);
            descLabel.getStyleClass().add("acmg_content_desc_label");
            descLabel.setMaxWidth(460);
            box.getChildren().add(descLabel);

            String massage = role.containsKey("message") ? (String)role.get("message") : null;
            if(!StringUtils.isEmpty(massage)) {
                Label msgLabel = new Label();
                msgLabel.setWrapText(true);
                msgLabel.setText("("+massage+")");
                msgLabel.getStyleClass().add("acmg_content_msg_label");
                msgLabel.setMaxWidth(460);
                box.getChildren().add(msgLabel);
            }
        }

        popOver.setContentNode(scrollPane);
        popOver.show(label, 10);
    }

    public static void openFilterPopOver(Label label, List<Object> list) {
        PopOver popOver = new PopOver();
        popOver.setArrowLocation(PopOver.ArrowLocation.BOTTOM_CENTER);
        popOver.setHeaderAlwaysVisible(true);
        popOver.setAutoHide(true);
        popOver.setAutoFix(true);
        popOver.setDetachable(true);
        popOver.setArrowSize(15);
        popOver.setMaxSize(460, 150);
        popOver.setPrefSize(460, 150);
        popOver.setMinSize(460, 150);
        popOver.setTitle("Filter List");

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setMinSize(460, 150);
        scrollPane.setPrefSize(460, 150);
        scrollPane.setMaxSize(460, 150);

        VBox box = new VBox();
        box.setMaxWidth(445);
        box.getStyleClass().add("acmg_content_box");

        for(Object obj : list) {
            String option = obj.toString();
            if(obj.toString().contains(" ")) {
                String key = option.substring(0, option.indexOf(' '));
                String value = option.substring(option.indexOf(' ') + 1);
                setKeyValue(key, value, box);
            } else {
                if(option.equalsIgnoreCase("dbsnpRsId")) {
                    createHBox("dbSNP", "has dbSNP ID", box);
                }
                if(option.equalsIgnoreCase("cosmicIds")) {
                    createHBox("COSMIC", "has COSMIC ID", box);
                }
            }
        }

        scrollPane.setContent(box);
        popOver.setContentNode(scrollPane);
        popOver.show(label, 0);
    }

    private static void createHBox(String key, String value, VBox vBox) {
        HBox hBox = new HBox();
        hBox.setPrefWidth(435);
        Label label = new Label(key + " : " + value);
        label.getStyleClass().add("label");
        hBox.getChildren().add(label);
        vBox.getChildren().add(hBox);
        VBox.setMargin(hBox, new Insets(5,5,0,10));
    }

    private static String alleSet(String value) {
        Pattern p = Pattern.compile("\\d+");
        Matcher m;
        List<String> values = new ArrayList<>();
        m = p.matcher(value);
        while (m.find()) {
            values.add(m.group());
        }
        if(value.contains("and")) {
            return values.get(0) + " ~ " + values.get(1);
        } else if(value.contains("gt")) {
            return values.get(0) + " ~ ";
        } else {
            return " ~ " + values.get(0);
        }

    }

    private static String setFeqTextField(String option) {
        return "≥ " + option.substring(option.indexOf(":") + 1);
    }

    private static void setKeyValue(String key, String value, VBox box) {
        if(key.equalsIgnoreCase("tier") || key.equalsIgnoreCase("pathogenicity")) {
            createHBox(key, value, box);
        } else if(key.equalsIgnoreCase("clinVarClass")) {
            createHBox("ClinVar", value, box);
        } else if(key.equalsIgnoreCase("codingConsequence")) {
            createHBox("Consequence", value.replaceAll("_", " "), box);
        } else if(key.equalsIgnoreCase("gene")) {
            createHBox(key, value, box);
        } else if(key.equalsIgnoreCase("chromosome")) {
            createHBox(key, value, box);
        } else if(key.equalsIgnoreCase("alleleFraction")) {
            createHBox(key, alleSet(value), box);
        } else if(key.equalsIgnoreCase("variantType")) {
            if(value.equalsIgnoreCase("snp")) {
                createHBox("Variant Type", "snv", box);
            } else if(value.equalsIgnoreCase("ins")) {
                createHBox("Variant Type", "ins", box);
            } else if(value.equalsIgnoreCase("del")) {
                createHBox("Variant Type", "del", box);
            }
        } else if(key.equalsIgnoreCase("g1000All")) {
            createHBox("1KGP All",setFeqTextField(value), box);
        }else if(key.equalsIgnoreCase("g1000African")) {
            createHBox("1KGP AFR",setFeqTextField(value), box);
        }else if(key.equalsIgnoreCase("g1000American")) {
            createHBox("1KGP AMR",setFeqTextField(value), box);
        }else if(key.equalsIgnoreCase("g1000EastAsian")) {
            createHBox("1KGP EAS",setFeqTextField(value), box);
        }else if(key.equalsIgnoreCase("g1000European")) {
            createHBox("1KGP UER",setFeqTextField(value), box);
        }else if(key.equalsIgnoreCase("g1000SouthAsian")) {
            createHBox("1KGP SAS",setFeqTextField(value), box);
        }else if(key.equalsIgnoreCase("esp6500All")) {
            createHBox("esp6500 All",setFeqTextField(value), box);
        }else if(key.equalsIgnoreCase("esp6500aa")) {
            createHBox("esp6500 AA",setFeqTextField(value), box);
        }else if(key.equalsIgnoreCase("esp6500ea")) {
            createHBox("esp6500 EA",setFeqTextField(value), box);
        }else if(key.equalsIgnoreCase("koreanExomInformationDatabase")) {
            createHBox("koreanExomInformationDatabase",setFeqTextField(value), box);
        }else if(key.equalsIgnoreCase("koreanReferenceGenomeDatabase")) {
            createHBox("koreanReferenceGenomeDatabase",setFeqTextField(value), box);
        }else if(key.equalsIgnoreCase("kohbraFreq")) {
            createHBox("kohbraFreq",setFeqTextField(value), box);
        }else if(key.equalsIgnoreCase("exac")) {
            createHBox("exac",setFeqTextField(value), box);
        }else if(key.equalsIgnoreCase("genomADall")) {
            createHBox("genomAD All",setFeqTextField(value), box);
        }else if(key.equalsIgnoreCase("genomADadmixedAmerican")) {
            createHBox("genomAD Admixed American",setFeqTextField(value), box);
        }else if(key.equalsIgnoreCase("genomADafricanAfricanAmerican")) {
            createHBox("genomAD African African American",setFeqTextField(value), box);
        }else if(key.equalsIgnoreCase("genomADashkenaziJewish")) {
            createHBox("genomAD Ashkenazi Jewish",setFeqTextField(value), box);
        }else if(key.equalsIgnoreCase("genomADeastAsian")) {
            createHBox("genomAD East Asian",setFeqTextField(value), box);
        }else if(key.equalsIgnoreCase("genomADfinnish")) {
            createHBox("genomAD Finnish",setFeqTextField(value), box);
        }else if(key.equalsIgnoreCase("genomADnonFinnishEuropean")) {
            createHBox("genomAD Non Finnish European",setFeqTextField(value), box);
        }else if(key.equalsIgnoreCase("genomADothers")) {
            createHBox("genomAD Others",setFeqTextField(value), box);
        }else if(key.equalsIgnoreCase("genomADsouthAsian")) {
            createHBox("genomAD SouthAsian",setFeqTextField(value), box);
        } else if(key.equalsIgnoreCase("cosmicOccurrence")) {
            createHBox("cosmic Occurrence", value.replaceAll("_", " "), box);
        }
    }

}
