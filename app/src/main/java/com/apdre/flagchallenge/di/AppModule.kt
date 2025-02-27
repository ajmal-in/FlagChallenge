package com.apdre.flagchallenge.di

import android.content.Context
import androidx.room.Room
import com.apdre.flagchallenge.database.ChallengeDatabase
import com.apdre.flagchallenge.model.repo.Repository
import com.apdre.flagchallenge.model.repo.RepositoryImpl
import com.apdre.flagchallenge.model.repo.datasource.local.LocalDataSource
import com.apdre.flagchallenge.model.repo.datasource.local.LocalDataSourceImpl
import com.apdre.flagchallenge.model.repo.datasource.remote.ApiService
import com.apdre.flagchallenge.model.repo.datasource.remote.ApiServiceImpl
import com.apdre.flagchallenge.model.repo.datasource.remote.BackendApiRetroService
import com.apdre.flagchallenge.model.repo.datasource.remote.RemoteDataSource
import com.apdre.flagchallenge.model.repo.datasource.remote.RemoteDataSourceImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Cache
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideLocalDataSource(@ApplicationContext context: Context): LocalDataSource {
        val localDataSource: LocalDataSource = LocalDataSourceImpl(context)
        return localDataSource
    }

    @Provides
    @Singleton
    fun provideRepository(
        @ApplicationContext context: Context,
        localDataSource: LocalDataSource,
        remoteDataSource: RemoteDataSource
    ): Repository {
        return RepositoryImpl(
            context = context,
            localDataSource = localDataSource,
            remoteDataSource = remoteDataSource
        )
    }

    @Provides
    @Singleton
    fun provideApiService(backendApiRetroService: BackendApiRetroService): ApiService {
        return ApiServiceImpl(backendApiRetroService)
    }

    @Provides
    @Singleton
    fun provideRemoteDataSource(apiService: ApiService): RemoteDataSource {
        return RemoteDataSourceImpl(apiService)
    }

    @Provides
    @Singleton
    fun provideRetrofit(@ApplicationContext context: Context): BackendApiRetroService {

        val client = OkHttpClient.Builder().build()

        val retrofit = Retrofit.Builder().baseUrl(ApiService.BASEURL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client).build()

        return retrofit.create(BackendApiRetroService::class.java)
    }

    @Provides
    @Singleton
    fun provideRoomDatabase(@ApplicationContext context: Context) : ChallengeDatabase =
        Room.databaseBuilder(context, ChallengeDatabase::class.java, "challenges_db").build()


    @Provides
    @Singleton
    fun provideChallengeDao(challengeDatabase: ChallengeDatabase) = challengeDatabase.challengeDao()

}