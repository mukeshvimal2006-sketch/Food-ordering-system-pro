package foodorder.util;

import java.awt.Color;
import java.awt.Font;

/** Central color & font palette for the redesigned, animated TastyHub Pro UI. */
public class UITheme {

    // Brand palette - warm food-delivery tones
    public static final Color ORANGE        = new Color(255, 111, 0);
    public static final Color ORANGE_DARK   = new Color(230, 81, 0);
    public static final Color ORANGE_LIGHT  = new Color(255, 167, 38);
    public static final Color RED_ACCENT    = new Color(229, 57, 53);
    public static final Color GREEN_ACCENT  = new Color(67, 160, 71);
    public static final Color YELLOW_ACCENT = new Color(255, 202, 40);

    public static final Color INK           = new Color(33, 33, 41);
    public static final Color INK_SOFT      = new Color(90, 90, 102);
    public static final Color SURFACE       = new Color(255, 255, 255);
    public static final Color BACKDROP      = new Color(250, 246, 240);
    public static final Color CARD_BORDER   = new Color(238, 230, 219);
    public static final Color SHADOW        = new Color(0, 0, 0, 40);

    public static final Font DISPLAY  = new Font("Segoe UI", Font.BOLD, 32);
    public static final Font HEADLINE = new Font("Segoe UI", Font.BOLD, 22);
    public static final Font TITLE    = new Font("Segoe UI", Font.BOLD, 16);
    public static final Font BODY     = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font BODY_BOLD = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font SMALL    = new Font("Segoe UI", Font.PLAIN, 12);
    public static final Font TINY     = new Font("Segoe UI", Font.PLAIN, 11);

    private UITheme() {}
}
