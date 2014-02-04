package org.fixprotocol.test.fix.ui.table;

import java.awt.BorderLayout;
import java.awt.Container;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;

import org.fixprotocol.test.fix.FIXField;
import org.fixprotocol.test.fix.FIXProtocol;

import quickfix.ConfigError;


public class FIXFieldTablePanelTest {

    public static void main(String[] args) {
        // List<FIXField> fixFields =
        // createFields("8=FIX.4.4|9=232|35=D|50=MILLENNIUM1|49=MLPHK|56=JPMFOUAT|34=869|52=20131114-05:44:48.539|11=458DXUSGE20131114004448|40=2|59=0|1=93006P00|21=1|22=5|54=1|55=SCFZ3|48=SCFZ3|167=FUT|200=201312|461=FXXXXX|38=5|44=5000|100=XSGE|77=O|60=20131114-05:44:48|10=124|");
        List<FIXField> fixFields = createFields("8=FIX.4.2|9=0257|35=D|49=GRAHAMV3|56=JPMUAT|34=1120|52=20131114-17:07:52|57=JPMKONA4|142=US,CT|50=TST|167=FUT|60=20131114-17:07:51|59=0|55=ES|54=1|1028=N|48=ES|40=1|38=100|6063=20131114-18:49:32|22=5|200=201412|21=1|100=XCME|15=USD|11=sshukla.20131114.0000|6061=TWAP|1=TEST|10=053|");

        FIXFieldTableModel model = new FIXFieldTableModel(fixFields);
        FIXFieldTablePanel tablePanel = new FIXFieldTablePanel();
        tablePanel.setData(fixFields);

        JFrame frame = new JFrame("Test");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Container cp = frame.getContentPane();
        cp.setLayout(new BorderLayout());
        cp.add(tablePanel);

        frame.pack();
        frame.setLocationByPlatform(true);
        frame.setVisible(true);
    }

    public static List<FIXField> createFields(String messageString) {
        messageString = FIXProtocol.normalize(messageString);
        try {
            return FIXProtocol.getInstance().getFields(messageString);
        } catch (ConfigError | IOException e) {
            return new ArrayList<FIXField>();
        }
    }

    public static List<FIXField> createData() {
        List<FIXField> fixFields = new ArrayList<FIXField>();
        fixFields.add(new FIXField("", 8, "FIX.4.4"));
        fixFields.add(new FIXField("", 9, "235"));
        fixFields.add(new FIXField("", 35, "D"));
        fixFields.add(new FIXField("", 52, "20131030-16:46:28.968"));
        fixFields.add(new FIXField("", 34, "61"));
        fixFields.add(new FIXField("", 49, "CARGILL02"));
        fixFields.add(new FIXField("", 56, "JPMUAT"));
        fixFields.add(new FIXField("", 50, "BMCDEVITT"));
        fixFields.add(new FIXField("", 142, "US,MN"));
        fixFields.add(new FIXField("", 1, "&17568K10CQ"));
        fixFields.add(new FIXField("", 11, "JOSE-20131030-B040"));
        fixFields.add(new FIXField("", 55, "KE"));
        fixFields.add(new FIXField("", 1151, "KE"));
        fixFields.add(new FIXField("", 200, "201405"));
        fixFields.add(new FIXField("", 207, "XKBT"));
        fixFields.add(new FIXField("", 461, "FXXXXX"));
        fixFields.add(new FIXField("", 44, "683"));
        fixFields.add(new FIXField("", 38, "5"));
        fixFields.add(new FIXField("", 21, "2"));
        fixFields.add(new FIXField("", 40, "2"));
        fixFields.add(new FIXField("", 54, "2"));
        fixFields.add(new FIXField("", 59, "0"));
        fixFields.add(new FIXField("", 60, "20131017-13:50:18"));
        fixFields.add(new FIXField("", 1028, "Y"));
        fixFields.add(new FIXField("", 10318, "LYNX# 6752"));
        fixFields.add(new FIXField("", 10, "143"));

        return fixFields;
    }

}
