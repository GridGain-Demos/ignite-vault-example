package com.gridgain.ecosystem.vault;

import com.bettercloud.vault.Vault;
import com.bettercloud.vault.VaultConfig;
import com.bettercloud.vault.VaultException;
import org.apache.ignite.IgniteCheckedException;
import org.apache.ignite.plugin.security.SecurityCredentials;
import org.apache.ignite.plugin.security.SecurityCredentialsProvider;

public class VaultSecurityCredentialsProvider implements SecurityCredentialsProvider {
    private String vaultAddress;
    private String vaultToken;
    private String passwordPath;
    private String username;

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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public SecurityCredentials credentials() throws IgniteCheckedException {
        try {
            final VaultConfig vaultConfig = new VaultConfig()
                    .address(this.vaultAddress)
                    .token(this.vaultToken)
                    .build();
            final Vault vault = new Vault(vaultConfig);
            final String password = vault.logical().read(this.passwordPath).getData().get("password");
            return new SecurityCredentials(username,password);
        }
        catch (VaultException e) {
            return null;
        }
    }
}
