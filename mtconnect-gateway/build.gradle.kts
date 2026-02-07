dependencies {
    implementation(project(":mtconnect-common"))
    compileOnly("com.inductiveautomation.ignitionsdk:gateway-api:${property("ignitionVersion")}")
}
