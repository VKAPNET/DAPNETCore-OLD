/*
 * DAPNET CORE PROJECT
 * Copyright (C) 2015
 *
 * Daniel Sialkowski
 *
 * daniel.sialkowski@rwth-aachen.de
 *
 * Institut für Hochfrequenztechnik
 * RWTH AACHEN UNIVERSITY
 * Melatener Str. 25
 * 52074 Aachen
 */

package org.dapnet.core.transmission;

import java.io.Serializable;

public class TransmissionSettings implements Serializable {
    private PagingProtocolSettings pagingProtocolSettings = new PagingProtocolSettings();
    private Raspager1Settings raspager1Settings = new Raspager1Settings();

    public PagingProtocolSettings getPagingProtocolSettings() {
        return pagingProtocolSettings;
    }

    public Raspager1Settings getRaspager1Settings() {
        return raspager1Settings;
    }

    public class PagingProtocolSettings implements Serializable{
        private int numberOfSyncLoops = 5;
        private int sendSpeed = 1;// 0: 512, 1: 1200, 2:2400

        public int getNumberOfSyncLoops() {
            return numberOfSyncLoops;
        }

        public int getSendSpeed() {
            return sendSpeed;
        }
    }

    public class Raspager1Settings implements Serializable{
        private int maxNumberOfReconnects = 5;
        private int reconnectWaitTime = 20 * 1000;
        private int connectionTimeout = 5000;
        private int maxMessageCount = 4;
        private int transmissionDelay = 500;

        public int getConnectionTimeout() {
            return connectionTimeout;
        }

        public int getReconnectWaitTime() {
            return reconnectWaitTime;
        }

        public int getMaxNumberOfReconnects() {
            return maxNumberOfReconnects;
        }

        public int getMaxMessageCount() {
            return maxMessageCount;
        }

        public int getTransmissionDelay() {
            return transmissionDelay;
        }
    }
}