/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2015  California Department of Transportation
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
package us.mn.state.dot.tms.client.camera;

import java.util.EventListener;

/**
 * Listens for actions performed on the active camera by this client.
 *
 * @author Dan Rossiter
 */
public interface CameraActionListener extends EventListener {
    /** Fires when an action is performed on the camera */
    void actionPerformed();
}
