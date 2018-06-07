package simulations.default

object Environments {

    val baseUrl : String = scala.util.Properties.envOrElse("BASE_URL","http://localhost:8080")

}