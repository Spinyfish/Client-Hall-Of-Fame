package dev.echo.other.auth;

import dev.echo.other.auth.service.AlteningServiceType;
import dev.echo.other.auth.service.ServiceSwitcher;

public final class TheAlteningAuthentication {

    private static TheAlteningAuthentication instance;
    private final ServiceSwitcher serviceSwitcher = new ServiceSwitcher();
    private final SSLController sslController = new SSLController();
    private AlteningServiceType service;

    private TheAlteningAuthentication(AlteningServiceType service) {
        this.updateService(service);
    }

    public static TheAlteningAuthentication mojang() {
        return withService(AlteningServiceType.MOJANG);
    }

    public static TheAlteningAuthentication altening() {
        return withService(AlteningServiceType.THEALTENING);
    }

    private static TheAlteningAuthentication withService(AlteningServiceType service) {
        if (instance == null) {
            instance = new TheAlteningAuthentication(service);
        } else if (instance.getService() != service) {
            instance.updateService(service);
        }

        return instance;
    }

    public void updateService(AlteningServiceType service) {
        if (service == null || this.service == service) {
            return;
        }

        switch (service) {
            case MOJANG:
                this.sslController.enableCertificateValidation();
                break;

            case THEALTENING:
                this.sslController.disableCertificateValidation();
                break;
        }

        this.service = this.serviceSwitcher.switchToService(service);
    }

    public AlteningServiceType getService() {
        return service;
    }
}