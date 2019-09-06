package com.idea.jgw.logic.btc.model;

public class TLOperationsManager {
    public enum TLDownloadState {
        NotDownloading,
        QueuedForDownloading,
        Downloading,
        Downloaded,
        Failed
    }
}
