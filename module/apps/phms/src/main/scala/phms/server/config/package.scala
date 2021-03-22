package phms.server

import sprout.SproutSub

package object config {
  type APIRoot = APIRoot.Type
  object APIRoot extends SproutSub[String]
}
