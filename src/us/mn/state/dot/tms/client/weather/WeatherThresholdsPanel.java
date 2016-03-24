/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2016 California Department of Transportation
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */
package us.mn.state.dot.tms.client.weather;

import us.mn.state.dot.sonar.client.ProxyListener;
import us.mn.state.dot.sonar.client.TypeCache;
import us.mn.state.dot.tms.SystemAttrEnum;
import us.mn.state.dot.tms.SystemAttribute;
import us.mn.state.dot.tms.client.Session;
import us.mn.state.dot.tms.utils.I18N;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;


/**
 * A panel for viewing and editing weather sensor threshold configuration.
 *
 * @author Dan Rossiter
 */
public class WeatherThresholdsPanel extends JPanel {

    /** User session */
    private final Session session;

    /** System attribute type cache */
    private final TypeCache<SystemAttribute> sys_attrs;

    /** Low color button */
    private final JButton low_color_button = new JButton();

    /** Mid color button */
    private final JButton mid_color_Button = new JButton();

    /** High color button */
    private final JButton high_color_button = new JButton();

    /** Low air temp text field */
    private final JTextField low_air_tmp_text = new JTextField();

    /** High air temp text field */
    private final JTextField high_air_tmp_text = new JTextField();

    /** Low precipitation rate text field */
    private final JTextField low_precip_rate_text = new JTextField();

    /** High precipitation rate text field */
    private final JTextField high_precip_rate_text = new JTextField();

    /** Low wind speed text field */
    private final JTextField low_wind_speed_text = new JTextField();

    /** High wind speed text field */
    private final JTextField high_wind_speed_text = new JTextField();

    /** Low visibility text field */
    private final JTextField low_visibility_text = new JTextField();

    /** High visibility text field */
    private final JTextField high_visibility_text = new JTextField();

    /** System attribute proxy listener */
    private final ProxyListener<SystemAttribute> listener = new ProxyListener<SystemAttribute>() {
        @Override
        public void proxyAdded(SystemAttribute proxy) { }

        @Override
        public void enumerationComplete() { }

        @Override
        public void proxyRemoved(SystemAttribute proxy) { }

        /** Monitor proxies for all fields being displayed */
        @Override
        public void proxyChanged(SystemAttribute proxy, String a) {
            if (SystemAttrEnum.RWIS_COLOR_LOW.aname().equals(proxy.getName())) {
                low_color_button.setBackground(SystemAttrEnum.RWIS_COLOR_LOW.getColor());
            } else if (SystemAttrEnum.RWIS_COLOR_MID.aname().equals(proxy.getName())) {
                mid_color_Button.setBackground(SystemAttrEnum.RWIS_COLOR_MID.getColor());
            } else if (SystemAttrEnum.RWIS_COLOR_HIGH.aname().equals(proxy.getName())) {
                high_color_button.setBackground(SystemAttrEnum.RWIS_COLOR_HIGH.getColor());
            } else if (SystemAttrEnum.RWIS_LOW_AIR_TEMP_C.aname().equals(proxy.getName())) {
                low_air_tmp_text.setText(proxy.getValue());
            } else if (SystemAttrEnum.RWIS_HIGH_AIR_TEMP_C.aname().equals(proxy.getName())) {
                high_air_tmp_text.setText(proxy.getValue());
            } else if (SystemAttrEnum.RWIS_LOW_PRECIP_RATE_MMH.aname().equals(proxy.getName())) {
                low_precip_rate_text.setText(proxy.getValue());
            } else if (SystemAttrEnum.RWIS_HIGH_PRECIP_RATE_MMH.aname().equals(proxy.getName())) {
                high_precip_rate_text.setText(proxy.getValue());
            } else if (SystemAttrEnum.RWIS_LOW_VISIBILITY_DISTANCE_M.aname().equals(proxy.getName())) {
                low_visibility_text.setText(proxy.getValue());
            } else if (SystemAttrEnum.RWIS_HIGH_VISIBILITY_DISTANCE_M.aname().equals(proxy.getName())) {
                high_visibility_text.setText(proxy.getValue());
            }
        }
    };

    /** Create new weather thresholds panel */
    public WeatherThresholdsPanel(Session s) {
        super(new GridBagLayout());
        session = s;
        sys_attrs = s.getSonarState().getSystemAttributes();
        setBorder(BorderFactory.createTitledBorder(I18N.get(
            "weather.thresholds")));
    }

    /** Initialize thresholds panel */
    public void initialize() {
        initColors();
        initTextFields();

        sys_attrs.addProxyListener(listener);

        GridBagConstraints gc = new GridBagConstraints();
        gc.fill = GridBagConstraints.BOTH;
        gc.gridheight = 4;
        gc.gridy = 0;
        gc.gridx = 1;
        add(low_color_button, gc);
        gc.gridx = 3;
        add(mid_color_Button, gc);
        gc.gridx = 5;
        add(high_color_button, gc);

        gc = new GridBagConstraints();
        gc.gridx = 0;
        gc.gridy = 0;
        add(new JLabel("Air Temp (\u00B0C)"), gc);
        gc.gridy = 1;
        add(new JLabel("Precipitation (mmh)"), gc);
        gc.gridy = 2;
        add(new JLabel("Visibility (m)"), gc);
        gc.gridy = 3;
        add(new JLabel("Wind Speed (kph)"), gc);

        gc.gridx = 2;
        gc.gridy = 0;
        add(low_air_tmp_text, gc);
        gc.gridy = 1;
        add(low_precip_rate_text, gc);
        gc.gridy = 2;
        add(low_visibility_text, gc);
        gc.gridy = 3;
        add(low_wind_speed_text, gc);

        gc.gridx = 4;
        gc.gridy = 0;
        add(high_air_tmp_text, gc);
        gc.gridy = 1;
        add(high_precip_rate_text, gc);
        gc.gridy = 2;
        add(high_visibility_text, gc);
        gc.gridy = 3;
        add(high_wind_speed_text, gc);
    }

    /** Initialize the threshold color fields */
    private void initColors() {
        low_color_button.setBackground(SystemAttrEnum.RWIS_COLOR_LOW.getColor());
        low_color_button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                changeRwisColor(SystemAttrEnum.RWIS_COLOR_LOW, "Choose RWIS Low Color");
            }
        });

        mid_color_Button.setBackground(SystemAttrEnum.RWIS_COLOR_MID.getColor());
        mid_color_Button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                changeRwisColor(SystemAttrEnum.RWIS_COLOR_MID, "Choose RWIS Mid Color");
            }
        });

        high_color_button.setBackground(SystemAttrEnum.RWIS_COLOR_HIGH.getColor());
        high_color_button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                changeRwisColor(SystemAttrEnum.RWIS_COLOR_HIGH, "Choose RWIS High Color");
            }
        });
    }

    /** Initialize the threshold text fields */
    private void initTextFields() {
        low_air_tmp_text.setText(Float.toString(SystemAttrEnum.RWIS_LOW_AIR_TEMP_C.getFloat()));
        low_air_tmp_text.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) { }

            @Override
            public void focusLost(FocusEvent e) {
                thresholdFocusLost(e, SystemAttrEnum.RWIS_LOW_AIR_TEMP_C, low_air_tmp_text);
            }
        });

        high_air_tmp_text.setText(Float.toString(SystemAttrEnum.RWIS_HIGH_AIR_TEMP_C.getFloat()));
        high_air_tmp_text.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) { }

            @Override
            public void focusLost(FocusEvent e) {
                thresholdFocusLost(e, SystemAttrEnum.RWIS_HIGH_AIR_TEMP_C, high_air_tmp_text);
            }
        });

        low_precip_rate_text.setText(Integer.toString(SystemAttrEnum.RWIS_LOW_PRECIP_RATE_MMH.getInt()));
        low_precip_rate_text.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) { }

            @Override
            public void focusLost(FocusEvent e) {
                thresholdFocusLost(e, SystemAttrEnum.RWIS_LOW_PRECIP_RATE_MMH, low_precip_rate_text);
            }
        });

        high_precip_rate_text.setText(Integer.toString(SystemAttrEnum.RWIS_HIGH_PRECIP_RATE_MMH.getInt()));
        high_precip_rate_text.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) { }

            @Override
            public void focusLost(FocusEvent e) {
                thresholdFocusLost(e, SystemAttrEnum.RWIS_HIGH_PRECIP_RATE_MMH, high_precip_rate_text);
            }
        });

        low_visibility_text.setText(Integer.toString(SystemAttrEnum.RWIS_LOW_VISIBILITY_DISTANCE_M.getInt()));
        low_visibility_text.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) { }

            @Override
            public void focusLost(FocusEvent e) {
                thresholdFocusLost(e, SystemAttrEnum.RWIS_LOW_VISIBILITY_DISTANCE_M, low_visibility_text);
            }
        });

        high_visibility_text.setText(Integer.toString(SystemAttrEnum.RWIS_HIGH_VISIBILITY_DISTANCE_M.getInt()));
        high_visibility_text.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) { }

            @Override
            public void focusLost(FocusEvent e) {
                thresholdFocusLost(e, SystemAttrEnum.RWIS_HIGH_VISIBILITY_DISTANCE_M, high_visibility_text);
            }
        });

        low_wind_speed_text.setText(Integer.toString(SystemAttrEnum.RWIS_LOW_WIND_SPEED_KPH.getInt()));
        low_wind_speed_text.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) { }

            @Override
            public void focusLost(FocusEvent e) {
                thresholdFocusLost(e, SystemAttrEnum.RWIS_LOW_WIND_SPEED_KPH, low_wind_speed_text);
            }
        });

        high_wind_speed_text.setText(Integer.toString(SystemAttrEnum.RWIS_HIGH_WIND_SPEED_KPH.getInt()));
        high_wind_speed_text.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) { }

            @Override
            public void focusLost(FocusEvent e) {
                thresholdFocusLost(e, SystemAttrEnum.RWIS_HIGH_WIND_SPEED_KPH, high_wind_speed_text);
            }
        });
    }

    /** Update corresponding system attribute */
    private void thresholdFocusLost(FocusEvent e, SystemAttrEnum a, JTextField tf) {
        if (e.isTemporary()) return;

        for (SystemAttribute attr : sys_attrs)
            if (attr.getName().equals(a.aname()))
                attr.setValue(tf.getText());
    }

    /** Change the color of an RWIS color system attribute */
    private void changeRwisColor(SystemAttrEnum a, String t) {
        Color c = JColorChooser.showDialog(this, t, a.getColor());
        if (c == null) return;

        for (SystemAttribute attr : sys_attrs) {
            if (a.aname().equals(attr.getName())) {
                attr.setValue(Integer.toHexString(c.getRGB()));
                break;
            }
        }
    }

    /** Dispose of the panel */
    public void dispose() {
        sys_attrs.removeProxyListener(listener);
    }

    /** Set enabled */
    public void setEnabled(boolean e) {
        for (Component c : getComponents())
            if (c instanceof JButton || c instanceof JTextField)
                c.setEnabled(e);

        super.setEnabled(e);
    }
}
