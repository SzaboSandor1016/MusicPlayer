pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "MusicPlayer"
include(":app")
include(":features")
include(":features:songs")
include(":features:songs:data")
include(":features:songs:domain")
include(":datasources")
include(":datasources:database")
include(":datasources:database:dao")
include(":datasources:mediastore")
include(":datasources:mediastore:data")
include(":datasources:mediastore:domain")
include(":features:playlists")
include(":features:playlists:data")
include(":features:playlists:domain")
include(":features:musicsource")
include(":features:musicsource:data")
include(":features:musicsource:domain")
include(":features:songs:presentation")
include(":features:playlists:presentation")
include(":core")
include(":core:ui")
include(":di")
include(":core:common")
include(":features:sync")
include(":features:sync:domain")
