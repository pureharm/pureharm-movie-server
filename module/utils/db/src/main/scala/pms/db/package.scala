package pms

import doobie.{free, syntax, util, Aliases}
import pms.core.Spook

package object db
  extends Aliases with doobie.hi.Modules with doobie.free.Modules with doobie.free.Types with free.Instances
  with syntax.AllSyntax with util.meta.SqlMeta with util.meta.TimeMetaInstances with util.meta.MetaConstructors {

  implicit class MetaOps[T](m: Meta[T]) {
    def haunt[PT](implicit x: Spook[T, PT]): Meta[PT] = m.imap(x.spook)(x.despook)
  }
}
