package br.com.ubisafe.alertprocessor.domain.notification.model;

public enum NotificationStatus {
    RECEBIDO(false),
    PROCESSANDO(false),
    PROCESSADO(true),
    FALHA(true);

    private final boolean terminal;

    NotificationStatus(boolean terminal) {
        this.terminal = terminal;
    }

    public boolean isTerminal() {
        return terminal;
    }
}
