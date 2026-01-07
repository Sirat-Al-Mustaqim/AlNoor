package com.siratalmustaqim.alnoor.di

import android.content.Context
import com.siratalmustaqim.alnoor.data.preferences.SettingsDataStore
import com.siratalmustaqim.alnoor.data.repository.GuardRepository
import com.siratalmustaqim.alnoor.vpn.VpnManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module for providing VPN and Guard related dependencies
 */
@Module
@InstallIn(SingletonComponent::class)
object VpnModule {
    
    @Provides
    @Singleton
    fun provideVpnManager(
        @ApplicationContext context: Context
    ): VpnManager {
        return VpnManager(context)
    }
    
    @Provides
    @Singleton
    fun provideGuardRepository(
        vpnManager: VpnManager,
        settingsDataStore: SettingsDataStore
    ): GuardRepository {
        return GuardRepository(vpnManager, settingsDataStore)
    }
}
