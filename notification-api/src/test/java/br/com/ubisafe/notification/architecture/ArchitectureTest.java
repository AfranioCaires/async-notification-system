package br.com.ubisafe.notification.architecture;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.Architectures.layeredArchitecture;

@AnalyzeClasses(packages = "br.com.ubisafe.notification", importOptions = ImportOption.DoNotIncludeTests.class)
class ArchitectureTest {

    private static final String DOMAIN = "br.com.ubisafe.notification.domain..";
    private static final String APPLICATION = "br.com.ubisafe.notification.application..";
    private static final String INFRASTRUCTURE = "br.com.ubisafe.notification.infrastructure..";
    private static final String PRESENTATION = "br.com.ubisafe.notification.presentation..";

    @ArchTest
    static final ArchRule layersRespectDependencyRule = layeredArchitecture()
            .consideringOnlyDependenciesInLayers()
            .layer("Domain").definedBy(DOMAIN)
            .layer("Application").definedBy(APPLICATION)
            .layer("Infrastructure").definedBy(INFRASTRUCTURE)
            .layer("Presentation").definedBy(PRESENTATION)
            .whereLayer("Presentation").mayNotBeAccessedByAnyLayer()
            .whereLayer("Infrastructure").mayNotBeAccessedByAnyLayer()
            .whereLayer("Application").mayOnlyBeAccessedByLayers("Presentation", "Infrastructure")
            .whereLayer("Domain").mayOnlyBeAccessedByLayers("Application", "Presentation", "Infrastructure");

    @ArchTest
    static final ArchRule domainIsFrameworkFree = noClasses().that().resideInAPackage(DOMAIN)
            .should().dependOnClassesThat()
            .resideInAnyPackage("org.springframework..", "jakarta..", "com.fasterxml..", "org.apache.kafka..", "org.hibernate..");

    @ArchTest
    static final ArchRule applicationIsFrameworkFree = noClasses().that().resideInAPackage(APPLICATION)
            .should().dependOnClassesThat()
            .resideInAnyPackage("org.springframework..", "jakarta..", "com.fasterxml..", "org.apache.kafka..", "org.hibernate..");

    @ArchTest
    static final ArchRule useCasesLiveInUseCasePackages = classes()
            .that().haveSimpleNameEndingWith("UseCase")
            .should().resideInAPackage("..application.*.usecase");

    @ArchTest
    static final ArchRule controllersLiveInControllerPackages = classes()
            .that().haveSimpleNameEndingWith("Controller")
            .should().resideInAPackage("..presentation.*.controller");

    @ArchTest
    static final ArchRule jpaEntitiesLiveInPersistencePackage = classes()
            .that().haveSimpleNameEndingWith("JpaEntity")
            .should().resideInAPackage("..infrastructure.persistence");

    @ArchTest
    static final ArchRule repositoryContractsLiveInDomain = classes()
            .that().resideInAPackage("..domain..").and().haveSimpleNameEndingWith("Repository")
            .should().beInterfaces()
            .andShould().resideInAPackage("..domain.*.repository");
}
