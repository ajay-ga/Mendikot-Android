package com.mendikotapp.di

import com.mendikotapp.data.repository.GameRepository
import com.mendikotapp.data.repository.TrickPlayingRepository
import com.mendikotapp.data.ai.BotAI
import com.mendikotapp.data.ai.BotDecisionLogger
import com.mendikotapp.data.scoring.RoundTracker
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    
    @Provides
    @Singleton
    fun provideGameRepository(): GameRepository {
        return GameRepository()
    }

    @Provides
    @Singleton
    fun provideTrickPlayingRepository(): TrickPlayingRepository {
        return TrickPlayingRepository()
    }

    @Provides
    @Singleton
    fun provideBotDecisionLogger(): BotDecisionLogger {
        return BotDecisionLogger()
    }

    @Provides
    @Singleton
    fun provideBotAI(logger: BotDecisionLogger): BotAI {
        return BotAI(logger)
    }

    @Provides
    @Singleton
    fun provideRoundTracker(): RoundTracker {
        return RoundTracker()
    }
} 