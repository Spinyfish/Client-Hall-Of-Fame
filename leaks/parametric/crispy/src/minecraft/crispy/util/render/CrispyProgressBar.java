package crispy.util.render;


import javax.swing.*;
import javax.swing.plaf.basic.BasicProgressBarUI;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;

public class CrispyProgressBar extends BasicProgressBarUI {
    int width;
    int height;

    public CrispyProgressBar() {
        this.width = 500;
        this.height = 26;
    }

    @Override
    protected Dimension getPreferredInnerVertical() {
        return new Dimension(this.height, this.width);
    }

    @Override
    protected Dimension getPreferredInnerHorizontal() {
        return new Dimension(this.width, this.height);
    }

    @Override
    protected void paintDeterminate(final Graphics g, final JComponent c) {
        final Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        final int iStrokWidth = 1;
        final int width = this.progressBar.getWidth();
        final int height = this.progressBar.getHeight();
        final Rectangle2D rect = new Rectangle2D.Double(0.0, 0.0, width, height);
        g2d.setColor(Color.BLACK);
        g2d.fill(rect);
        final int iInnerHeight = height - iStrokWidth * 4;
        int iInnerWidth = width - iStrokWidth * 4;
        g2d.setColor(new Color(-11448236));
        final RoundRectangle2D fill = new RoundRectangle2D.Double(0.0, 0.0, iInnerWidth, iInnerHeight, iInnerHeight, iInnerHeight);
        g2d.fill(fill);
        g2d.setColor(Color.BLACK);
        final RoundRectangle2D fill2 = new RoundRectangle2D.Double(1.0, 1.0, iInnerWidth - 2, iInnerHeight - 2, iInnerHeight - 2, iInnerHeight - 2);
        g2d.fill(fill2);
        iInnerWidth = (int) Math.round((iInnerWidth - 4) * this.getProgress());
        g2d.setColor(Color.GREEN);
        final RoundRectangle2D fill3 = new RoundRectangle2D.Double(2.0, 2.0, iInnerWidth, iInnerHeight - 4, iInnerHeight - 4, iInnerHeight - 4);
        g2d.fill(fill3);
        g2d.dispose();
    }

    private double getProgress() {
        final double dProgress = this.progressBar.getPercentComplete();
        if (dProgress < 0.05) {
            return 0.05;
        }
        if (dProgress > 1.0) {
            return 1.0;
        }
        return dProgress;
    }

    @Override
    protected void paintIndeterminate(final Graphics g, final JComponent c) {
        super.paintIndeterminate(g, c);
    }
}