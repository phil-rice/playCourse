package utilties

trait ToJson[T] extends (T => String)

object Json{
  implicit class JsonPimper[T](t: T)(implicit toJsonForT: ToJson[T]){
    def toJson = toJsonForT(t)
  }
}

