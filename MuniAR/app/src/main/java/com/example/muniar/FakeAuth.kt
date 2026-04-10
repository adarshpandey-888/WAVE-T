package com.example.muniar

object FakeAuth {

    private val users = listOf(

        User(
            username = "admin",
            name = "Admin User",
            role = "System Admin",
            company = "MuniAR Core Team",
            address = "Head Office, India",
            license = "ADMIN-001",
            email = "admin@muniar.com",
            phone = "+91 9999999999"
        ),

        User(
            username = "aadarsh",
            name = "Aadarsh Kumar",
            role = "Site Engineer",
            company = "Apex Infra",
            address = "Gurgaon, India",
            license = "LIC-AAD-221",
            email = "aadarsh@apex.com",
            phone = "+91 8888888888"
        ),

        User(
            username = "aashutosh",
            name = "Aashutosh Singh",
            role = "Field Supervisor",
            company = "Metro Build Corp",
            address = "Delhi NCR",
            license = "LIC-AAS-332",
            email = "aashu@metro.com",
            phone = "+91 7777777777"
        ),

        User(
            username = "komal",
            name = "Komal Sharma",
            role = "Project Manager",
            company = "Urban Grid Ltd.",
            address = "Noida, India",
            license = "LIC-KOM-443",
            email = "komal@urban.com",
            phone = "+91 6666666666"
        )
    )

    // 🔐 simple auth
    fun login(username: String, password: String): User? {
        return users.find { it.username == username && it.username == password }
    }
}