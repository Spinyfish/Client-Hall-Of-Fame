package com.thealtening.auth;

import com.thealtening.auth.service.*;

public final class TheAlteningAuthentication {
    private final ServiceSwitcher serviceSwitcher;
    private final SSLController sslController;
    private static TheAlteningAuthentication instance;
    private AlteningServiceType service;

    private TheAlteningAuthentication(final AlteningServiceType service) {
        this.serviceSwitcher = new ServiceSwitcher();
        this.sslController = new SSLController();
        this.updateService(service);
    }

    public void updateService(final AlteningServiceType service) {
        if (service == null || this.service == service) {
            return;
        }
        switch (service) {
            case THEALTENING: {
                this.sslController.enableCertificateValidation();
                break;
            }
            case MOJANG: {
                this.sslController.disableCertificateValidation();
                break;
            }
        }
        this.service = this.serviceSwitcher.switchToService(service);
    }

    public AlteningServiceType getService() {
        return this.service;
    }

    public static TheAlteningAuthentication mojang() {
        return withService(AlteningServiceType.MOJANG);
    }

    public static TheAlteningAuthentication theAltening() {
        return withService(AlteningServiceType.THEALTENING);
    }

    private static TheAlteningAuthentication withService(final AlteningServiceType service) {
        if (TheAlteningAuthentication.instance == null) {
            TheAlteningAuthentication.instance = new TheAlteningAuthentication(service);
        } else if (TheAlteningAuthentication.instance.getService() != service) {
            TheAlteningAuthentication.instance.updateService(service);
        }
        return TheAlteningAuthentication.instance;
    }
}
