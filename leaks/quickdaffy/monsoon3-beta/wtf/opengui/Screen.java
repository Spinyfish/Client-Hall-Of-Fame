/*
 * Decompiled with CFR 0.152.
 */
package wtf.opengui;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import net.minecraft.client.Minecraft;
import wtf.opengui.Element;
import wtf.opengui.IStyle;

public interface Screen {
    public static final List<Element> elements = new ArrayList<Element>();

    default public void build() {
    }

    default public void style(String getter, IStyle supStyle) {
        if (!getter.startsWith("#")) {
            elements.stream().filter(e -> e.getType().endsWith(getter)).forEach(e -> e.setStyle(supStyle.get()));
        } else {
            elements.stream().filter(e -> e.getId() != null).filter(e -> e.getId().equals(getter)).forEach(e -> {
                e.getStyle().width = supStyle.get().width == 0.0f ? e.getStyle().width : supStyle.get().width;
                e.getStyle().height = supStyle.get().height == 0.0f ? e.getStyle().height : supStyle.get().height;
                e.getStyle().radius = supStyle.get().radius == 0.0f ? e.getStyle().radius : supStyle.get().radius;
                e.getStyle().fill_color = supStyle.get().fill_color == new Color(0, true) ? e.getStyle().fill_color : supStyle.get().fill_color;
            });
        }
        elements.forEach(e -> {
            if (e != elements.get(0)) {
                Element lastE = elements.get(elements.indexOf(e) - 1);
                e.setY(lastE.getY());
                e.setX(lastE.getX() + lastE.getStyle().width);
                if (e.getX() + e.getStyle().width >= (float)Minecraft.getMinecraft().currentScreen.width) {
                    e.setX(0.0f);
                    Element biggestHeightE = (Element)elements.stream().sorted((e1, e2) -> (int)(e2.getStyle().height - e1.getStyle().height)).collect(Collectors.toList()).get(0);
                    e.setY(lastE.getY() + biggestHeightE.getStyle().height);
                }
            }
        });
    }

    default public Element ele(String ... vars) {
        Element e = Element.getByType(vars[0]);
        if (vars.length >= 2) {
            e.setID(vars[1]);
        }
        elements.add(e);
        return e;
    }
}

