/*
 * Copyright (c) 2018 - 2023, Entgra (Pvt) Ltd. (http://www.entgra.io) All Rights Reserved.
 *
 * Entgra (Pvt) Ltd. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */


package io.entgra.device.mgt.core.certificate.mgt.core.cache.impl;

import io.entgra.device.mgt.core.certificate.mgt.core.cache.CertificateCacheManager;
import io.entgra.device.mgt.core.certificate.mgt.core.dto.CertificateResponse;
import io.entgra.device.mgt.core.device.mgt.core.config.DeviceConfigurationManager;
import io.entgra.device.mgt.core.device.mgt.core.config.DeviceManagementConfig;

import javax.cache.Cache;
import javax.cache.CacheConfiguration;
import javax.cache.CacheManager;
import javax.cache.Caching;
import java.util.concurrent.TimeUnit;

public class CertificateCacheManagerImpl implements CertificateCacheManager {

    public static final String CERTIFICATE_CACHE_MANAGER = "CERTIFICATE_CACHE_MANAGER";
    public static final String CERTIFICATE_CACHE = "CERTIFICATE_CACHE";
    private static boolean isCertificateCacheInitialized = false;
    private static String SERIAL_PRE = "S_";
    private static String COMMON_NAME_PRE = "C_";

    private static volatile CertificateCacheManager certificateCacheManager;


    private CertificateCacheManagerImpl() {
    }

    public static CertificateCacheManager getInstance() {
        if (certificateCacheManager == null) {
            synchronized (CertificateCacheManagerImpl.class) {
                if (certificateCacheManager == null) {
                    certificateCacheManager = new CertificateCacheManagerImpl();
                }
            }
        }
        return certificateCacheManager;
    }

    @Override
    public void addCertificateBySerial(String serialNumber, CertificateResponse certificate) {
        CertificateCacheManagerImpl.getCertificateCache().put(SERIAL_PRE + serialNumber, certificate);
    }

    @Override
    public void addCertificateByCN(String commonName, CertificateResponse certificate) {
        CertificateCacheManagerImpl.getCertificateCache().put(COMMON_NAME_PRE + commonName, certificate);
    }

    @Override
    public CertificateResponse getCertificateBySerial(String serialNumber) {
        return CertificateCacheManagerImpl.getCertificateCache().get(SERIAL_PRE + serialNumber);
    }

    @Override
    public CertificateResponse getCertificateByCN(String commonName) {
        return CertificateCacheManagerImpl.getCertificateCache().get(COMMON_NAME_PRE + commonName);
    }


    private static CacheManager getCacheManager() {
        return Caching.getCacheManagerFactory().getCacheManager(CertificateCacheManagerImpl.CERTIFICATE_CACHE_MANAGER);
    }

    public static Cache<String, CertificateResponse> getCertificateCache() {
        DeviceManagementConfig config = DeviceConfigurationManager.getInstance().getDeviceManagementConfig();
        CacheManager manager = getCacheManager();
        Cache<String, CertificateResponse> certificateCache = null;
        if (config.getDeviceCacheConfiguration().isEnabled()) {
            if (!isCertificateCacheInitialized) {
                initializeCertificateCache();
            }
            if (manager != null) {
                certificateCache = manager.getCache(CertificateCacheManagerImpl.CERTIFICATE_CACHE);
            } else {
                certificateCache = Caching.getCacheManager(CertificateCacheManagerImpl.CERTIFICATE_CACHE_MANAGER)
                        .getCache(CertificateCacheManagerImpl.CERTIFICATE_CACHE);
            }
        }
        return certificateCache;
    }

    public static void initializeCertificateCache() {
        DeviceManagementConfig config = DeviceConfigurationManager.getInstance().getDeviceManagementConfig();
        int certificateCacheExpiry = config.getCertificateCacheConfiguration().getExpiryTime();
        CacheManager manager = getCacheManager();
        if (config.getCertificateCacheConfiguration().isEnabled()) {
            if (!isCertificateCacheInitialized) {
                isCertificateCacheInitialized = true;
                if (manager != null) {
                    if (certificateCacheExpiry > 0) {
                        manager.createCacheBuilder(CertificateCacheManagerImpl.CERTIFICATE_CACHE).
                                setExpiry(CacheConfiguration.ExpiryType.MODIFIED, new CacheConfiguration.Duration(TimeUnit.SECONDS,
                                        certificateCacheExpiry)).setExpiry(CacheConfiguration.ExpiryType.ACCESSED, new CacheConfiguration.
                                Duration(TimeUnit.SECONDS, certificateCacheExpiry)).setStoreByValue(true).build();
                    } else {
                        manager.getCache(CertificateCacheManagerImpl.CERTIFICATE_CACHE);
                    }
                } else {
                    if (certificateCacheExpiry > 0) {
                        Caching.getCacheManager()
                                .createCacheBuilder(CertificateCacheManagerImpl.CERTIFICATE_CACHE).
                                setExpiry(CacheConfiguration.ExpiryType.MODIFIED, new CacheConfiguration.Duration(TimeUnit.SECONDS,
                                        certificateCacheExpiry)).setExpiry(CacheConfiguration.ExpiryType.ACCESSED, new CacheConfiguration.
                                Duration(TimeUnit.SECONDS, certificateCacheExpiry)).setStoreByValue(true).build();
                    } else {
                        Caching.getCacheManager().getCache(CertificateCacheManagerImpl.CERTIFICATE_CACHE);
                    }
                }
            }
        }
    }
}

