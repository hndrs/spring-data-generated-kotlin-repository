# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/), and this project adheres
to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Added

- Generates MongoRepository based on kotlin data class
- Allows to generate only RepositoryExtension with ```@GenerateRepository(extensionOnly = true)```
- generates findBy method with all data class properties
- generates findOneAndSave method with all data class properties
- ```@Options(updateOnSave = true)``` updates properties of type ```Instant``` with ```Instant.now()```
- ```@Options(exclude = true)``` excludes parameters from find function
