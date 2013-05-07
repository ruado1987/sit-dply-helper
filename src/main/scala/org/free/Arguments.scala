trait Arguments {

  def get(name: String) : Option[String]

  def getOrElse(name: String, default: => String) : String = {
    get(name) match {
      case Some(s) => s.substring(s.indexOf("=") + 1)
      case None => default
    }
  }
}

case class SimpleArguments(args: Array[String]) extends Arguments {

  def get(name: String) = {
    args.find(_.contains(name))
  }
}
