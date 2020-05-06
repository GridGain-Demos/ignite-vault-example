package com.gridgain.ecosystem.vault;

import com.bettercloud.vault.Vault;
import com.bettercloud.vault.VaultConfig;
import com.bettercloud.vault.VaultException;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

public class VaultDataSourceProxy implements DataSource {
    private DataSource proxy;
    private String vaultAddress;
    private String vaultToken;
    private String passwordPath;
    private String passwordProperty;

    public DataSource getProxy() {
        return proxy;
    }

    public void setProxy(DataSource proxy) {
        this.proxy = proxy;
        updatePassword();
    }

    public String getVaultAddress() {
        return vaultAddress;
    }

    public void setVaultAddress(String vaultAddress) {
        this.vaultAddress = vaultAddress;
    }

    public String getVaultToken() {
        return vaultToken;
    }

    public void setVaultToken(String vaultToken) {
        this.vaultToken = vaultToken;
    }

    public String getPasswordPath() {
        return passwordPath;
    }

    public void setPasswordPath(String passwordPath) {
        this.passwordPath = passwordPath;
    }

    public String getPasswordProperty() {
        return passwordProperty;
    }

    public void setPasswordProperty(String passwordProperty) {
        this.passwordProperty = passwordProperty;
    }

    @Override
    public Connection getConnection() throws SQLException {
        updatePassword();
        return proxy.getConnection();
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return proxy.unwrap(iface);
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return proxy.isWrapperFor(iface);
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        return proxy.getLogWriter();
    }

    @Override
    public void setLogWriter(PrintWriter out) throws SQLException {
        proxy.setLogWriter(out);
    }

    @Override
    public void setLoginTimeout(int seconds) throws SQLException {
        proxy.setLoginTimeout(seconds);
    }

    @Override
    public int getLoginTimeout() throws SQLException {
        return proxy.getLoginTimeout();
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return proxy.getParentLogger();
    }

    public Connection getConnection(String userID, String pass) throws SQLException {
        return proxy.getConnection(userID, getPassword());
    }

    public String getPassword() {
        try {
            final VaultConfig vaultConfig = new VaultConfig()
                    .address(this.vaultAddress)
                    .token(this.vaultToken)
                    .build();
            final Vault vault = new Vault(vaultConfig);
            final String password = vault.logical().read(this.passwordPath).getData().get("password");
            return password;
        }
        catch (VaultException e) {
            return null;
        }
    }

    public void setPassword(String pass) {
        // no-op
    }

    private void updatePassword() {
        // Can we fetch the password from Vault?
        if (vaultAddress == null || vaultToken == null || passwordPath == null || passwordProperty == null) {
            return;
        }

        // If so, let's do it
        String passwordSetterMethodName = "set" + passwordProperty.substring(0,1).toUpperCase() + passwordProperty.substring(1);
        try {
            Method passwordSetterMethod = proxy.getClass().getMethod(passwordSetterMethodName, String.class);
            passwordSetterMethod.invoke(proxy, this.getPassword());
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
