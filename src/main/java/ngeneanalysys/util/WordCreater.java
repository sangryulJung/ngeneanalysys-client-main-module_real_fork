package ngeneanalysys.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import ngeneanalysys.code.constants.CommonConstants;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.apache.xmlbeans.XmlException;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.*;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STBorder.Enum;

import ngeneanalysys.model.SampleQC;
import ngeneanalysys.model.SnpInDel;
import ngeneanalysys.model.VariantAndInterpretationEvidence;
import ngeneanalysys.model.VariantCountByGene;

/**
 * @author Jang
 * @since 2018-02-12
 */
public class WordCreater {
    private XWPFDocument doc;
    private String writeFilePath;
    private File docFile;
    private Map<String, Object> params = new HashMap<>();
    private final List<XWPFTable> deleteList = new ArrayList<>();
    private String dir = System.getProperty("user.dir");
    private String path = dir + "\\test.docx";
    private final Pattern keyFindPattern = Pattern.compile("\\$([^\\s]+)\\$");

    public void updateEmbeddedDoc(String path) throws OpenXML4JException, IOException {
        this.docFile = new File(path);
        if(!this.docFile.exists()) {
            throw new FileNotFoundException("The Word document" + /*path +*/ " does not exist.");
        }
        try(FileInputStream fis = new FileInputStream(docFile)) {
            doc = new XWPFDocument(fis);
        }
    }

    public void setQCData(List<XWPFTableCell> cells, SampleQC qc) {
        setTableCellText(cells.get(1), qc.getQcThreshold(), 8, false);
        setTableCellText(cells.get(2), qc.getQcUnit(), 8, false);
        setTableCellText(cells.get(3), qc.getQcValue().toString(), 8, false);
    }

    public void replaceText(XWPFRun run) {
        Set<String> keys = params.keySet();

        // do not trim()
        String replaceText = run.toString();
        Matcher m = keyFindPattern.matcher(replaceText);
        while(m.find()) {
            String findKey = m.group(1);
            if (keys.contains(findKey)) {
                String value = "";
                if (params.get(findKey) instanceof Integer) {
                    value = params.get(findKey).toString();
                } else {
                    value = (String) params.get(findKey);
                }
                replaceText = replaceText.replace("$" + findKey + "$", value);
            } else {
                replaceText = replaceText.replace("$" + findKey + "$", "");
            }
        }
        run.setText(replaceText, 0);
    }

    public void updateWordFile() throws OpenXML4JException, IOException {

        textEdit();
        tableEdit();
        if(!deleteList.isEmpty()) {
            for(int i = deleteList.size() - 1 ; i >= 0 ; i--) {
                deleteTable(deleteList.get(i));
            }
        }

        doc.write(new FileOutputStream(writeFilePath));
        //doc.write(new FileOutputStream(writeFilePath));
    }

    public void textEdit() {
        doc.getParagraphs().stream().forEach(p -> {
            final List<XWPFRun> runs = p.getRuns();
            if(runs != null) {
                runs.stream().forEach(r -> replaceText(r));
            }
        });
    }

    public String getCaption(final XWPFTable table) {
        if(table == null) return null;
        CTTblPr cttbl = table.getCTTbl().getTblPr();
        String[] ab = cttbl.toString().split("\n");

        for(int i = 0; i < ab.length ; i++) {
            if(ab[i].contains("<w:tblCaption")) {
                return ab[i].substring(ab[i].indexOf("\"") + 1, ab[i].lastIndexOf("\""));
            }
        }
        return null;
    }

    public int getTierCount(String tier) {

        if(StringUtils.isEmpty(tier)) {
            return 0;
        } else if(tier.equals("T1")) {
            return (Integer)params.get("tierOneCount");
        } else if(tier.equals("T2")) {
            return (Integer)params.get("tierTwoCount");
        } else if(tier.equals("T3")) {
            return (Integer)params.get("tierThreeCount");
        }
        return 0;
    }

    @SuppressWarnings("unchecked")
    public void setGenesInPanel(final XWPFTable table) {
        List<VariantCountByGene> genes = (List<VariantCountByGene>)params.get("variantCountByGenes");
        if(genes.isEmpty()) return;

        int halfCount = (genes.size() / 2) + 1;

        if(genes.size() % 2 == 0) {
            halfCount -= 1;
        }
        XWPFTableRow oldRow = table.getRow(1);
        for(int i = 0; i < halfCount ; i++) {
            CTRow ctrow;
            try {
                ctrow = CTRow.Factory.parse(oldRow.getCtRow().newInputStream());
                VariantCountByGene gene1 = genes.get(i);
                VariantCountByGene gene2 = null;
                if((halfCount + i) < genes.size()) gene2 = genes.get(halfCount + i);
                if(i == 0) {
                    if(i == (genes.size() -1)) {
                        setGenesInPanelRowStyle(oldRow, null, STBorder.DOUBLE);
                    } else {
                        setGenesInPanelRowStyle(oldRow, null, STBorder.NONE);
                    }

                    addGeneRowData(oldRow, gene1, gene2);

                } else {
                    XWPFTableRow newRow = new XWPFTableRow(ctrow, table);
                    if(i == (genes.size() -1)) {
                        setGenesInPanelRowStyle(newRow, STBorder.NONE, STBorder.DOUBLE);
                    } else {
                        setGenesInPanelRowStyle(newRow, STBorder.NONE, STBorder.NONE);
                    }

                    addGeneRowData(newRow, gene1, gene2);

                    table.addRow(newRow);
                }
            } catch (XmlException | IOException e) {
                e.printStackTrace();
            }
        }

    }

    public void addGeneRowData(XWPFTableRow row, VariantCountByGene gene1, VariantCountByGene gene2) {
        if(gene1 != null) {
            setTableCellText(row.getCell(0), gene1.getGeneSymbol(), 8, true);
            setTableCellText(row.getCell(1), String.valueOf(tier1VariantCount(gene1)), 8, false);
            setTableCellText(row.getCell(2), String.valueOf(tier2VariantCount(gene1)), 8, false);
            setTableCellText(row.getCell(3), String.valueOf(tier3VariantCount(gene1)), 8, false);
            setTableCellText(row.getCell(4), String.valueOf(totalCount(gene1)), 8, false);
        }

        if(gene2 != null) {
            setTableCellText(row.getCell(6), gene2.getGeneSymbol(), 8, true);
            setTableCellText(row.getCell(7), String.valueOf(tier1VariantCount(gene2)), 8, false);
            setTableCellText(row.getCell(8), String.valueOf(tier2VariantCount(gene2)), 8, false);
            setTableCellText(row.getCell(9), String.valueOf(tier3VariantCount(gene2)), 8, false);
            setTableCellText(row.getCell(10), String.valueOf(totalCount(gene2)), 8, false);
        } else {
            setTableCellText(row.getCell(6), null, 8, false);
            setTableCellText(row.getCell(7), null, 8, false);
            setTableCellText(row.getCell(8), null, 8, false);
            setTableCellText(row.getCell(9), null, 8, false);
            setTableCellText(row.getCell(10), null, 8, false);
        }
    }

    public int tier1VariantCount(VariantCountByGene gene) {
        return gene.getTier1IndelCount() + gene.getTier1SnpCount();
    }

    public int tier2VariantCount(VariantCountByGene gene) {
        return gene.getTier2IndelCount() + gene.getTier2SnpCount();
    }

    public int tier3VariantCount(VariantCountByGene gene) {
        return gene.getTier3IndelCount() + gene.getTier3SnpCount();
    }

    public int totalCount(VariantCountByGene gene) {
        return gene.getTier1IndelCount() + gene.getTier1SnpCount() + gene.getTier2IndelCount() + gene.getTier2SnpCount()
                + gene.getTier3IndelCount() + gene.getTier3SnpCount();
    }

    public void setQCData(final XWPFTable table) {
        List<XWPFTableRow> rows = table.getRows();

        final String[] item = new String[]{"zdnaQuality", "zinputDNA", "zpcrCycle", "ztotalHybDNA", "zclusterDensity"};
        final String[] qcItem = new String[]{"totalBase", "q30", "mappedBase", "duplicatedReads", "onTarget", "onTargetCoverage"};

        for(int i = 0 ; i < 5 ; i++)
            setTableCellText(rows.get(i + 1).getCell(3), params.get(item[i]) != null ? params.get(item[i]).toString() : null, 8, false);

        int arrayIdx = 0;
        for(int rowIdx = 6; rowIdx < 12; rowIdx++)
            setQCData(rows.get(rowIdx).getTableCells(), (SampleQC)params.get(qcItem[arrayIdx++]));

    }

    @SuppressWarnings("unchecked")
    public void setConclusions(final XWPFTable table) {
        List<String> con = (List<String>)params.get("conclusions");

        if(con != null && !con.isEmpty()) {
            XWPFTableCell cell = table.getRow(0).getCell(0);
            for(int i = 0; i < con.size() ; i++) {
                XWPFRun run = cell.getParagraphs().get(0).getRuns().get(0);
                if(i != 0) {
                    run = cell.addParagraph().createRun();
                }
                run.setText(con.get(i));
                run.setBold(false);
            }
        }
    }

    public void setMaterialInformation(final XWPFTable table) {
        simpleTableConvert(table);
    }

    public void setTableCellText(XWPFTableCell cell, String text, int fontSize, boolean bold) {
        XWPFRun run = null;
        ParagraphAlignment alignment = ParagraphAlignment.CENTER;
        if(!cell.getParagraphs().isEmpty()) {
            if(StringUtils.isEmpty(text)) {
                if(!cell.getParagraphs().get(0).getRuns().isEmpty()) {
                    run = cell.getParagraphs().get(0).getRuns().get(0);
                    run.setText(text, 0);
                }
                return;
            }
            alignment = cell.getParagraphs().get(0).getAlignment();
            cell.removeParagraph(0);
        }
        run = cell.addParagraph().createRun();
        cell.getParagraphs().get(0).setAlignment(alignment);
        run.setText(text, 0);
        run.setFontSize(fontSize);
        run.setBold(bold);
    }

    public void addVariantRowData(XWPFTableRow row, VariantAndInterpretationEvidence variant) {
        setTableCellText(row.getCell(0), variant.getSnpInDel().getGenomicCoordinate().getGene(), 8, true);
        setTableCellText(row.getCell(1), variant.getSnpInDel().getSnpInDelExpression().getTranscript(), 7, false);
        setTableCellText(row.getCell(2), variant.getSnpInDel().getSnpInDelExpression().getNtChange(), 7, false);
        setTableCellText(row.getCell(3), variant.getSnpInDel().getSnpInDelExpression().getAaChange(), 7, false);
        setTableCellText(row.getCell(4), variant.getSnpInDel().getReadInfo().getAlleleFraction() != null ?
                variant.getSnpInDel().getReadInfo().getAlleleFraction().toString() + "%" : null, 7, false);
        setTableCellText(row.getCell(5), variant.getSnpInDel().getReadInfo().getReadDepth() != null ?
                variant.getSnpInDel().getReadInfo().getReadDepth().toString() : "", 7, false);
        setTableCellText(row.getCell(6), variant.getSnpInDel().getSnpInDelExpression().getAaChange(), 7, false);
        setTableCellText(row.getCell(7), variant.getInterpretationEvidence() != null ?
                variant.getInterpretationEvidence().getClinicalVariantType() : null, 7, false);

    }

    public String returnTierString(SnpInDel snpInDel) {
        String tier = "";

        if((!StringUtils.isEmpty(snpInDel.getExpertTier()) && "T1".equals(snpInDel.getExpertTier()))
                || (StringUtils.isEmpty(snpInDel.getExpertTier()) && "T1".equals(snpInDel.getSwTier()))) {
            tier = "Tier I";
        } else if((!StringUtils.isEmpty(snpInDel.getExpertTier()) && "T2".equals(snpInDel.getExpertTier()))
                || (StringUtils.isEmpty(snpInDel.getExpertTier()) && "T2".equals(snpInDel.getSwTier()))) {
            tier = "Tier II";
        } else {
            tier = "Tier III";
        }

        return tier;
    }

    public void setDectectedGeneticVariants(final XWPFTable table, String tier) {

        int tierCount = getTierCount(tier);

        if(tierCount == 0) {
            deleteList.add(table);
            return;
        }

        @SuppressWarnings("unchecked")
        List<VariantAndInterpretationEvidence> list = (List<VariantAndInterpretationEvidence>)params.get("variantList");

        list = list.stream().filter(variant -> (!StringUtils.isEmpty(variant.getSnpInDel().getExpertTier()) && tier.equals(
                variant.getSnpInDel().getExpertTier())) || ((StringUtils.isEmpty(variant.getSnpInDel().getExpertTier()) &&
                tier.equals(variant.getSnpInDel().getSwTier())))).collect(Collectors.toList());

        if(list != null && !list.isEmpty()) {
            XWPFTableRow oldRow = table.getRow(table.getRows().size() - 1);
            for(int i = 0; i < list.size() ; i++) {
                CTRow ctrow;
                try {
                    ctrow = CTRow.Factory.parse(oldRow.getCtRow().newInputStream());
                    final VariantAndInterpretationEvidence variant = list.get(i);
                    if(i == 0) {
                        if(i == (list.size() -1)) {
                            setRowStyle(oldRow, null, STBorder.DOUBLE);
                        } else {
                            setRowStyle(oldRow, null, STBorder.NONE);
                        }
                        addVariantRowData(oldRow, variant);
                    } else {
                        XWPFTableRow newRow = new XWPFTableRow(ctrow, table);
                        if(i == (list.size() -1)) {
                            setRowStyle(newRow, STBorder.NONE, STBorder.DOUBLE);
                        } else {
                            setRowStyle(newRow, STBorder.NONE, STBorder.NONE);
                        }
                        addVariantRowData(newRow, variant);
                        table.addRow(newRow);
                    }
                } catch (XmlException | IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    public void setRowStyle(final XWPFTableRow row, final Enum topBorder, final Enum bottomBorder) {
        row.getTableCells().stream().forEach(cell -> {
            CTTc ctTc = cell.getCTTc();
            CTTcPr tcPr = ctTc.addNewTcPr();
            CTTcBorders border = tcPr.addNewTcBorders();

            if(topBorder != null) border.addNewTop().setVal(topBorder);
            if(bottomBorder != null) border.addNewBottom().setVal(bottomBorder);
        });
    }

    public void setGenesInPanelRowStyle(final XWPFTableRow row, final Enum topBorder, final Enum bottomBorder) {
        for(int i = 0; i < row.getTableCells().size() ; i++) {
            if(i == 5) continue;
            CTTc ctTc = row.getTableCells().get(i).getCTTc();
            CTTcPr tcPr = ctTc.addNewTcPr();
            CTTcBorders border = tcPr.addNewTcBorders();

            if(topBorder != null) border.addNewTop().setVal(topBorder);
            if(bottomBorder != null) border.addNewBottom().setVal(bottomBorder);
        };
    }

    public void simpleTableConvert(final XWPFTable table) {
        table.getRows().stream().forEach(row -> {
            row.getTableCells().stream().forEach(cell -> {
                cell.getParagraphs().stream().forEach(p -> {
                    fullRunText(p, p.getRuns(), "");
                });
            });
        });
    }

    public void fullRunText(XWPFParagraph para, List<XWPFRun> runs, String text) {
        if(runs.isEmpty()) return;
        int runSize = runs.size();

        if(runSize > 1) {
            for(int i = 0 ; i < runSize ; i++) {
                text += runs.get(i).toString();
            }
            for(int i = 1 ; i < runSize ; i++) {
                para.removeRun(1);
            }
            runs.get(0).setText(text, 0);
        }
        replaceText(runs.get(0));

    }

    public void setNameTable(final XWPFTable table) {
        simpleTableConvert(table);
    }

    @SuppressWarnings("unchecked")
    public void setClinicalSignificance(final XWPFTable table) {
        XWPFTableRow countRow = table.getRow(2);

        countRow.getTableCells().stream().forEach(cell -> cell.getParagraphs().stream().forEach(p -> fullRunText(p, p.getRuns(), "")));

        addEvidenceRow(table, table.getRow(3), (List<VariantAndInterpretationEvidence>)params.get("clinicalVariantList"));
    }

    public void setInformationTable(final XWPFTable table) {
        simpleTableConvert(table);
    }

    public void tableEdit() {
        doc.getTables().stream().forEach(tbl -> {
            String caption = getCaption(tbl);
            if(tbl != null && caption != null && caption.matches("T1|T2|T3")) {
                setDectectedGeneticVariants(tbl, caption);
            } else if(tbl != null && caption != null && caption.matches("nameTable")) {
                setNameTable(tbl);
            } else if(tbl != null && caption != null && caption.matches("informationTable")) {
                setInformationTable(tbl);
            } else if(tbl != null && caption != null && caption.matches("ClinicalSignificance")) {
                setClinicalSignificance(tbl);
            } else if(tbl != null && caption != null && caption.matches("QCData")) {
                setQCData(tbl);
            } else if(tbl != null && caption != null && caption.matches("MaterialInformation")) {
                setMaterialInformation(tbl);
            } else if(tbl != null && caption != null && caption.matches("Conclusion")) {
                setConclusions(tbl);
            } else if(tbl != null && caption != null && caption.matches("GenesInPanel")) {
                setGenesInPanel(tbl);
            }
        });
    }

    public void addEvidenceRow(XWPFTable table,XWPFTableRow row, List<VariantAndInterpretationEvidence> list) {
        if(list != null && !list.isEmpty()) {
            for(int i = 0; i < list.size() ; i++) {
                CTRow ctrow;
                try {
                    ctrow = CTRow.Factory.parse(row.getCtRow().newInputStream());
                    final VariantAndInterpretationEvidence variant = list.get(i);
                    CTVMerge vmerge = CTVMerge.Factory.newInstance();
                    if(i == 0) {
                        setRowStyle(row, STBorder.BASIC_WIDE_MIDLINE, STBorder.BASIC_WIDE_MIDLINE);
                        evidenceRowData(row, variant, "Therapeutic");
                        vmerge.setVal(STMerge.RESTART);
                        row.getCell(0).getCTTc().getTcPr().setVMerge(vmerge);
                    } else {
                        XWPFTableRow newRow = new XWPFTableRow(ctrow, table);
                        setRowStyle(newRow, STBorder.BASIC_WIDE_MIDLINE, STBorder.BASIC_WIDE_MIDLINE);
                        evidenceRowData(newRow, variant,"Therapeutic");
                        vmerge.setVal(STMerge.RESTART);
                        newRow.getCell(0).getCTTc().getTcPr().setVMerge(vmerge);
                        table.addRow(newRow);
                    }
                    CTVMerge vmerge1 = CTVMerge.Factory.newInstance();
                    vmerge1.setVal(STMerge.CONTINUE);
                    XWPFTableRow newRow = new XWPFTableRow(ctrow, table);
                    setRowStyle(newRow, STBorder.BASIC_WIDE_MIDLINE, STBorder.BASIC_WIDE_MIDLINE);
                    evidenceRowData(newRow, variant,"Diagnosis");
                    newRow.getCell(0).getCTTc().getTcPr().setVMerge(vmerge1);
                    table.addRow(newRow);
                    newRow = new XWPFTableRow(ctrow, table);
                    setRowStyle(newRow, STBorder.BASIC_WIDE_OUTLINE, STBorder.BASIC_WIDE_MIDLINE);
                    evidenceRowData(newRow, variant,"Prognosis");
                    newRow.getCell(0).getCTTc().getTcPr().setVMerge(vmerge1);
                    table.addRow(newRow);
                } catch (XmlException | IOException e) {
                    e.printStackTrace();
                }
            }
        } else {
            table.removeRow(3);
        }
    }

    public void evidenceRowData(XWPFTableRow row, VariantAndInterpretationEvidence variant, String impacts) {
        setTableCellText(row.getCell(1), impacts, 8, false);
        if(impacts.equalsIgnoreCase("Therapeutic")) {
            setVariantInformation(row.getCell(0), variant);
            setTableCellText(row.getCell(2), variant.getInterpretationEvidence().getEvidenceLevelA(), 8, false);
            setTableCellText(row.getCell(3), variant.getInterpretationEvidence().getEvidenceLevelB(), 8, false);
            setTableCellText(row.getCell(4), variant.getInterpretationEvidence().getEvidenceLevelC(), 8, false);
            setTableCellText(row.getCell(5), variant.getInterpretationEvidence().getEvidenceLevelD(), 8, false);
        }
    }

    public void setVariantInformation(XWPFTableCell cell, VariantAndInterpretationEvidence variant) {
        if(cell == null) return;
        if(cell.getText() != null) cell.setText(null);
        if(!cell.getParagraphs().isEmpty()) {
            int paraIndex = cell.getParagraphs().size();
            for(int i = 0; i < paraIndex; i++) {
                cell.removeParagraph(0);
            }
        }
        XWPFRun run = cell.addParagraph().createRun();

        run.setText(variant.getSnpInDel().getGenomicCoordinate().getGene(), 0);
        run.setBold(true);
        run.setFontSize(9);
        if(variant.getSnpInDel().getSnpInDelExpression() != null && !StringUtils.isEmpty(variant.getSnpInDel().getSnpInDelExpression().getVariantType())) {
            run = cell.addParagraph().createRun();
            run.setText(variant.getSnpInDel().getSnpInDelExpression().getVariantType(), 0);
            run.setBold(false);
            run.setFontSize(8);
        }
        if(variant.getSnpInDel().getSnpInDelExpression() != null && !StringUtils.isEmpty(variant.getSnpInDel().getSnpInDelExpression().getNtChange())) {
            run = cell.addParagraph().createRun();
            run.setText(variant.getSnpInDel().getSnpInDelExpression().getNtChange(), 0);
            run.setBold(false);
            run.setFontSize(8);
        }
    }


    public void deleteTable(XWPFTable table) {
        int i = doc.getPosOfTable(table);
        doc.removeBodyElement(i);
    }

    public void setParams(Map<String, Object> params) {
        this.params = params;
    }

    public void setWriteFilePath(String path) {
        this.writeFilePath = path;
    }

    private static class WordCreaterHelper{
        private WordCreaterHelper(){}
        private static final WordCreater INSTANCE = new WordCreater();
    }

    public static WordCreater getInstance() {
        return WordCreaterHelper.INSTANCE;
    }
}
