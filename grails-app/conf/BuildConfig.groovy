grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir = "target/test-reports"
grails.project.target.level = 1.6

grails.project.fork = [
    //  compile: [maxMemory: 256, minMemory: 64, debug: false, maxPerm: 256, daemon:true],
    test: false,
    run: [maxMemory: 768, minMemory: 64, debug: false, maxPerm: 256, forkReserve:false],
    war: [maxMemory: 768, minMemory: 64, debug: false, maxPerm: 256, forkReserve:false],
    console: [maxMemory: 768, minMemory: 64, debug: false, maxPerm: 256]
]

grails.project.dependency.resolver = "maven"
grails.project.dependency.resolution = {
    inherits "global"
    log "warn"
    repositories {
        grailsCentral()
        mavenLocal()
        mavenCentral()
    }
    dependencies {
    }

    plugins {
        build(":release:3.0.1",
                ":rest-client-builder:1.0.3") {
            export = false
        }
        test(":hibernate4:4.3.6.1") {
            export = false
        }

        test(":codenarc:0.22") { export = false }
        test(":code-coverage:2.0.3-3") { export = false }

        compile(":platform-core:1.0.0") { excludes 'resources' }
    }
}
