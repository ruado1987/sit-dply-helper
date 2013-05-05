package org.free

import java.util.{ Date }

object `package` {

  trait DateFormatter {

    import org.joda.time.format.{ DateTimeFormat }

    val pattern : String
    lazy val formatter = DateTimeFormat.forPattern( pattern )

    def format( date : Date ) = {
      formatter print date.getTime
    }
  }
}
