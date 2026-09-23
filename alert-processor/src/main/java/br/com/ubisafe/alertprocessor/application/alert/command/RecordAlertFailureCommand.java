package br.com.ubisafe.alertprocessor.application.alert.command;

public record RecordAlertFailureCommand(AlertData alert, String reason) {
}
