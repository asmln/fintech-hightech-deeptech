plugins {
	java
	alias(libs.plugins.spring.boot) apply false
	alias(libs.plugins.spring.dependency.management) apply false
}

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(25)
	}
}

allprojects {
	group = "com.github.asmln"
	version = "0.0.1-SNAPSHOT"

	repositories {
		mavenCentral()
	}
}



