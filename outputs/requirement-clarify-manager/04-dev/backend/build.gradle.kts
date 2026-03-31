import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    val kotlinVersion = "1.9.25"
    id("org.springframework.boot") version "3.2.0"
    id("io.spring.dependency-management") version "1.1.4"
    id("org.openapi.generator") version "7.4.0"
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.spring") version kotlinVersion
    // JPA 플러그인 제거됨 (jOOQ 로 교체)
}

group = "org.openapitools"
version = "1.0.0"
java.sourceCompatibility = JavaVersion.VERSION_17

repositories {
    mavenCentral()
}

// ─── 1. OpenAPI 코드 생성 경로 ────────────────────────────────────────
val openapiGeneratedDir = layout.buildDirectory.dir("generated/openapi").get().asFile.absolutePath

openApiGenerate {
    generatorName.set("kotlin-spring")
    inputSpec.set("$rootDir/src/main/resources/openapi.yaml")
    outputDir.set(openapiGeneratedDir)
    apiPackage.set("org.openapitools.api")
    modelPackage.set("org.openapitools.model")
    configOptions.set(mapOf(
        "delegatePattern" to "true",
        "useSpringBoot3" to "true",
        "useBeanValidation" to "true",
        "serializationLibrary" to "jackson",
        "exceptionHandler" to "false"
    ))
}

// ─── 2. 생성 후 패치 Task (HTML entity bug 수정 + Application.kt 삭제) ─
val patchGeneratedSources by tasks.registering {
    inputs.dir("$openapiGeneratedDir/src")
    outputs.dir("$openapiGeneratedDir/src")
    dependsOn("openApiGenerate")
    doLast {
        val apiDir = file("$openapiGeneratedDir/src/main/kotlin")
        if (!apiDir.exists()) return@doLast
        fileTree(apiDir)
            .filter { it.extension == "kt" }
            .forEach { f ->
                val text = f.readText()
                if (text.contains("&#x60;")) {
                    f.writeText(text.replace("&#x60;", "`"))
                }
            }
        listOf(
            "$openapiGeneratedDir/src/main/kotlin/org/openapitools/Application.kt",
            "$openapiGeneratedDir/src/main/kotlin/org/openapitools/SpringDocConfiguration.kt"
        ).map(::file).filter(File::exists).forEach(File::delete)
    }
}

// ─── 3. 소스셋 추가 ───────────────────────────────────────────────────
kotlin {
    sourceSets {
        main {
            kotlin.srcDir("$openapiGeneratedDir/src/main/kotlin")
            // jOOQ 생성 코드 경로 추가 (예정)
            kotlin.srcDir(layout.buildDirectory.dir("generated-sources/jooq"))
        }
    }
}

tasks.named("compileKotlin") {
    dependsOn(patchGeneratedSources)
}

// ─── 4. 의존성 ────────────────────────────────────────────────────────
dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    // JPA 제거 후 jOOQ 추가
    implementation("org.springframework.boot:spring-boot-starter-jooq")

    // Kotlin
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    // Database
    runtimeOnly("com.h2database:h2")
    runtimeOnly("org.postgresql:postgresql")

    // OpenAPI UI (Swagger)
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.3.0")

    // Testing
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("io.kotest:kotest-runner-junit5:5.8.0")
    testImplementation("io.kotest:kotest-assertions-core:5.8.0")
    testImplementation("io.mockk:mockk:1.13.8")
    testImplementation("com.ninja-squad:springmockk:4.0.2")
}

// ─── 5. jOOQ 코드 생성 (H2-in-memory 방식 사용 권장) ───────────────────────
// 참고: 이 프로젝트에서는 단순화를 위해 수동 Record 작성을 피하기 위해 기본 설정 제공
// 실제 운영 시 jooq-codegen-gradle 플러그인 설정 권장

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs += "-Xjsr305=strict"
        jvmTarget = "17"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
