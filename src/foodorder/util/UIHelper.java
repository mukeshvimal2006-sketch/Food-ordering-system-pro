package foodorder.util;

import javax.swing.UIManager;

/** Small shared UI bootstrap helpers (look-and-feel setup, etc). */
public class UIHelper {

    private UIHelper() {}

    public static void applyNimbus() {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    return;
                }
            }
        } catch (Exception ignored) {
            // fall back to default look and feel
        }
    }
}
