package pashkinmv.gse.components;

import pashkinmv.gse.model.Key;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.WindowConstants;
import java.awt.HeadlessException;

public class MainFrame extends JFrame {
    private static final String TITLE = "GSettings configuration GUI tool";
    private static final int WINDOW_WIDTH = 900;
    private static final int WINDOW_HEIGHT = 600;

    public MainFrame() throws HeadlessException {
        super(TITLE);

        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        final JTabbedPane tabbedPane = new JTabbedPane();
        final BrowsePanel browsePanel = new BrowsePanel();
        final SearchPanel searchPanel = new SearchPanel();

        searchPanel.addActionListener(new SearchPanel.ActionListener() {
            @Override
            public void goToKeyRequired(Key key) {
                tabbedPane.setSelectedComponent(browsePanel);
                browsePanel.selectKey(key);
            }
        });

        tabbedPane.addTab("Browse", browsePanel);
        tabbedPane.addTab("Search", searchPanel);

        add(tabbedPane);
    }
}
