package ngeneanalysys.util;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
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

    private static HBox getTextItemBox(String title) {
        HBox hBox = new HBox();
        Label label = new Label(title.replace(":", " : "));
        label.getStyleClass().add("txt_black");
        hBox.getChildren().add(label);
        return hBox;
    }

    public static void openFalsePopOver(Label label, String text) {

        VBox box = new VBox();
        box.setStyle("-fx-padding:10;");
        if(org.apache.commons.lang3.StringUtils.isEmpty(text) || "NONE".equals(text)) {
            box.setAlignment(Pos.CENTER);
            Label emptyLabel = new Label("< Empty Warning Reason >");
            box.getChildren().add(emptyLabel);
        } else {
            box.setAlignment(Pos.BOTTOM_LEFT);
            String[] items = text.split(",");
            for(String item : items) {
                HBox hbox = getTextItemBox(item);
                box.getChildren().add(hbox);
            }
        }

        label.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            PopOver popOver = new PopOver();
            popOver.setArrowLocation(PopOver.ArrowLocation.LEFT_TOP);
            popOver.setHeaderAlwaysVisible(true);
            popOver.setAutoHide(true);
            popOver.setAutoFix(true);
            popOver.setDetachable(true);
            popOver.setArrowSize(15);
            popOver.setArrowIndent(30);
            popOver.setContentNode(box);
            popOver.show(label);
        });
    }

    public static void openToolTipPopOver(Label label, String text) {

        VBox box = new VBox();
        box.setStyle("-fx-padding:10;");
        box.setAlignment(Pos.CENTER);
        Label emptyLabel = new Label(text);
        box.getChildren().add(emptyLabel);

        label.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            PopOver popOver = new PopOver();
            popOver.setArrowLocation(PopOver.ArrowLocation.LEFT_TOP);
            popOver.setHeaderAlwaysVisible(true);
            popOver.setAutoHide(true);
            popOver.setAutoFix(true);
            popOver.setDetachable(true);
            popOver.setArrowSize(15);
            popOver.setArrowIndent(30);
            popOver.setContentNode(box);
            popOver.show(label);
        });
    }

    public static void openQCPopOver(Label label, String value) {
        if(StringUtils.isEmpty(value)) return;
        PopOver popOver = new PopOver();
        popOver.setArrowLocation(PopOver.ArrowLocation.BOTTOM_CENTER);
        popOver.setHeaderAlwaysVisible(true);
        popOver.setAutoHide(true);
        popOver.setAutoFix(true);
        popOver.setDetachable(true);
        popOver.setArrowSize(15);
        popOver.setPrefSize(240, 80);
        popOver.setMinSize(240, 80);
        popOver.setMaxSize(300, 80);

        VBox box = new VBox();
        box.setMinWidth(240);
        box.setPrefWidth(240);
        box.setMaxWidth(300);

        Label contents = new Label();
        contents.setPadding(new Insets(0, 0, 0, 10));
        contents.setAlignment(Pos.TOP_LEFT);
        contents.setPrefHeight(80);
        contents.setPrefWidth(240);
        contents.setMaxWidth(300);
        contents.setWrapText(true);

        contents.setText(value);

        box.getChildren().add(contents);

        popOver.setContentNode(box);
        popOver.show(label, 0);
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
        String operator = "";
        if(option.contains("gte:")) {
            operator = "≥ ";
        } else if(option.contains("lte:")) {
            operator = "≤ ";
        } else if(option.contains("gt:")) {
            operator = "> ";
        } else if(option.contains("lt:")) {
            operator = "< ";
        }
        return operator + option.substring(option.indexOf(':') + 1);
    }

    private static void setKeyValue(String key, String value, VBox box) {
        if(key.equalsIgnoreCase("Tier") || key.equalsIgnoreCase("Pathogenicity")) {
            createHBox("Prediction", value, box);
        } else if(key.equalsIgnoreCase("expertTier")) {
            createHBox("Tier", value, box);
        } else if(key.equalsIgnoreCase("expertPathogenicity")) {
            createHBox("Pathogenicity", value, box);
        } else if(key.equalsIgnoreCase("clinVarClass")) {
            createHBox("ClinVar", value, box);
        } else if(key.equalsIgnoreCase("codingConsequence")) {
            createHBox("Consequence", value, box);
        } else if(key.equalsIgnoreCase("gene")) {
            createHBox(key, value, box);
        } else if(key.equalsIgnoreCase("chromosome")) {
            createHBox(key, value, box);
        } else if(key.equalsIgnoreCase("alleleFraction")) {
            createHBox(key, alleSet(value), box);
        } else if(key.equalsIgnoreCase("variantType")) {
            String keyValue = "Variant Type";
            if (value.equals("snp") || value.equals("snv")) {
                value = "SNV";
            }else if(value.equals("ins")) {
                value = "Ins";
            }else if(value.equals("del")) {
                value = "Del";
            }else if(value.equals("complex")) {
                value = "Complex";
            }else if(value.equals("mnp")) {
                value = "MNP";
            }
            createHBox(keyValue, value, box);
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
            createHBox("koEXID",setFeqTextField(value), box);
        }else if(key.equalsIgnoreCase("koreanReferenceGenomeDatabase")) {
            createHBox("kRGDB",setFeqTextField(value), box);
        }else if(key.equalsIgnoreCase("kohbraFreq")) {
            createHBox("kOHBRA",setFeqTextField(value), box);
        }else if(key.equalsIgnoreCase("exac")) {
            createHBox("ExAC",setFeqTextField(value), box);
        }else if(key.equalsIgnoreCase("gnomADall")) {
            createHBox("gnomAD All",setFeqTextField(value), box);
        }else if(key.equalsIgnoreCase("gnomADadmixedAmerican")) {
            createHBox("gnomAD Admixed American",setFeqTextField(value), box);
        }else if(key.equalsIgnoreCase("gnomADafricanAfricanAmerican")) {
            createHBox("gnomAD African African American",setFeqTextField(value), box);
        }else if(key.equalsIgnoreCase("gnomADeastAsian")) {
            createHBox("gnomAD East Asian",setFeqTextField(value), box);
        }else if(key.equalsIgnoreCase("gnomADfinnish")) {
            createHBox("gnomAD Finnish",setFeqTextField(value), box);
        }else if(key.equalsIgnoreCase("gnomADnonFinnishEuropean")) {
            createHBox("gnomAD Non Finnish European",setFeqTextField(value), box);
        }else if(key.equalsIgnoreCase("gnomADothers")) {
            createHBox("gnomAD Others",setFeqTextField(value), box);
        }else if(key.equalsIgnoreCase("gnomADsouthAsian")) {
            createHBox("gnomAD SouthAsian",setFeqTextField(value), box);
        }else if(key.equalsIgnoreCase("cosmicOccurrence")) {
            createHBox("cosmic Occurrence", value.replace("_", " "), box);
        }else if(key.equalsIgnoreCase("lowConfidence")) {
            createHBox("Low Confidence", value, box);
        }else if(key.equalsIgnoreCase("readDepth")) {
            createHBox("Depth", alleSet(value), box);
        }else if(key.equalsIgnoreCase("altReadNum")) {
            createHBox("Alt Count",alleSet(value), box);
        }
    }

}
