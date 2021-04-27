package phms.json

import phms.*
import phms.json.{*, given}

trait SproutCirceEnc[O](using Encoder[O]) {self: _root_.sprout.Burry[O] =>
  given Encoder[self.Type] = Encoder[O].contramap(this.oldType)
}

trait SproutCirceDec[O](using Decoder[O]) {self: Sprout[O] =>
  given Decoder[self.Type] = Decoder[O].map(this.newType)
}

trait SproutRefinedCirceDec[O](using Decoder[O]) {self: SproutRefinedThrow[O] =>
  given Decoder[self.Type] = Decoder[O].emapTry(this.newType[Try])
}

extension [O](enc: Encoder[O]) {
  def sprout[N](using nt: OldType[O, N]): Encoder[N] = enc.contramap(nt.oldType)
}

extension[O](dec: Decoder[O]){
  def sprout[N](using nt: NewType[O, N]): Decoder[N] = dec.imap(nt.newType)(nt.oldType)
  def sproutRefined[N](using nt: RefinedTypeThrow[O, N]): Decoder[N] = dec.emapTry(nt.newType[Try])
}
