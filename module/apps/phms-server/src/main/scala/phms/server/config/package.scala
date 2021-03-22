package phms.server

import sprout.SproutSub

package object config {
  type APIRoot = APIRoot.Type
  object APIRoot extends SproutSub[String]

  type BootstrapServer = BootstrapServer.Type

  object BootstrapServer extends SproutSub[Boolean] {
    val True:  BootstrapServer = newType(true)
    val False: BootstrapServer = newType(false)
  }
}
