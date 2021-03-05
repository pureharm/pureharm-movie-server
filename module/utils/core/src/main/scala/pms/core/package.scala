package pms

import busymachines.pureharm.PureharmCoreTypeDefinitions
import busymachines.pureharm.anomaly.PureharmAnomalyTypeDefinitions
import scala.{concurrent => sc}

import cats.{effect => ce}
import cats.syntax
import cats.instances
/**
  *
  * @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 05 Apr 2019
  *
  */
package object core
  extends PureharmCoreTypeDefinitions with PureharmAnomalyTypeDefinitions with syntax.AllSyntax
  with syntax.AllSyntaxBinCompat0 with syntax.AllSyntaxBinCompat1 with syntax.AllSyntaxBinCompat2
  with syntax.AllSyntaxBinCompat3 with syntax.AllSyntaxBinCompat4 with syntax.AllSyntaxBinCompat5
  with syntax.AllSyntaxBinCompat6 with instances.AllInstances with instances.AllInstancesBinCompat0
  with instances.AllInstancesBinCompat1 with instances.AllInstancesBinCompat2 with instances.AllInstancesBinCompat3
  with instances.AllInstancesBinCompat4 with instances.AllInstancesBinCompat5 with instances.AllInstancesBinCompat6
  with EffectsSyntax.Implicits {
  //----------- handy custom types -----------
  final type Attempt[+R] = scala.util.Either[Throwable, R]
  final val Attempt: Either.type = scala.util.Either

  final type Functor[F[_]] = cats.Functor[F]
  final val Functor: cats.Functor.type = cats.Functor

  final type Applicative[F[_]] = cats.Applicative[F]
  final val Applicative: cats.Applicative.type = cats.Applicative

  final type Apply[F[_]] = cats.Apply[F]
  final val Apply: cats.Apply.type = cats.Apply

  final type FlatMap[F[_]] = cats.FlatMap[F]
  final val FlatMap: cats.FlatMap.type = cats.FlatMap

  final type CoflatMap[F[_]] = cats.CoflatMap[F]
  final val CoflatMap: cats.CoflatMap.type = cats.CoflatMap

  final type Monad[F[_]] = cats.Monad[F]
  final val Monad: cats.Monad.type = cats.Monad

  final type ApplicativeError[F[_], E] = cats.ApplicativeError[F, E]
  final val ApplicativeError: cats.ApplicativeError.type = cats.ApplicativeError

  final type ApplicativeThrow[F[_]] = cats.ApplicativeThrow[F]
  final val ApplicativeThrow: cats.ApplicativeThrow.type = cats.ApplicativeThrow

  final type MonadError[F[_], E] = cats.MonadError[F, E]
  final val MonadError: cats.MonadError.type = cats.MonadError

  final type MonadThrow[F[_]] = cats.MonadThrow[F]
  final val MonadThrow: cats.MonadThrow.type = cats.MonadThrow

  final type Traverse[F[_]] = cats.Traverse[F]
  final val Traverse: cats.Traverse.type = cats.Traverse

  final type NonEmptyTraverse[F[_]] = cats.NonEmptyTraverse[F]
  final val NonEmptyTraverse: cats.NonEmptyTraverse.type = cats.NonEmptyTraverse

  final type UnorderedTraverse[F[_]] = cats.UnorderedTraverse[F]
  final val UnorderedTraverse: cats.UnorderedTraverse.type = cats.UnorderedTraverse

  final type TraverseFilter[F[_]] = cats.TraverseFilter[F]
  final val TraverseFilter: cats.TraverseFilter.type = cats.TraverseFilter

  final type Bitraverse[F[_, _]] = cats.Bitraverse[F]
  final val Bitraverse: cats.Bitraverse.type = cats.Bitraverse

  final type Parallel[F[_]] = cats.Parallel[F]
  final val Parallel: cats.Parallel.type = cats.Parallel

  final type NonEmptyParallel[F[_]] = cats.NonEmptyParallel[F]
  final val NonEmptyParallel: cats.NonEmptyParallel.type = cats.NonEmptyParallel

  final type Semigroupal[F[_]] = cats.Semigroupal[F]
  final val Semigroupal: cats.Semigroupal.type = cats.Semigroupal

  final type Eq[A] = cats.Eq[A]
  final val Eq: cats.Eq.type = cats.Eq

  final type PartialOrder[A] = cats.PartialOrder[A]
  final val PartialOrder: cats.PartialOrder.type = cats.PartialOrder

  final type Comparison = cats.Comparison
  final val Comparison: cats.Comparison.type = cats.Comparison

  final type Order[A] = cats.Order[A]
  final val Order: cats.Order.type = cats.Order

  final type Hash[A] = cats.Hash[A]
  final val Hash: cats.Hash.type = cats.Hash

  final type Semigroup[A] = cats.Semigroup[A]
  final val Semigroup: cats.Semigroup.type = cats.Semigroup

  final type Monoid[A] = cats.Monoid[A]
  final val Monoid: cats.Monoid.type = cats.Monoid

  final type Group[A] = cats.Group[A]
  final val Group: cats.Group.type = cats.Group

  final type Eval[+A] = cats.Eval[A]
  final val Eval: cats.Eval.type = cats.Eval

  final type Now[A] = cats.Now[A]
  final val Now: cats.Now.type = cats.Now

  final type Later[A] = cats.Later[A]
  final val Later: cats.Later.type = cats.Later

  final type Always[A] = cats.Always[A]
  final val Always: cats.Always.type = cats.Always

  //---------- monad transformers ----------------

  final type EitherT[F[_], L, R] = cats.data.EitherT[F, L, R]
  final val EitherT: cats.data.EitherT.type = cats.data.EitherT

  final type OptionT[F[_], A] = cats.data.OptionT[F, A]
  final val OptionT: cats.data.OptionT.type = cats.data.OptionT

  //---------- cats-data ----------------
  final type NEList[+A] = cats.data.NonEmptyList[A]
  final val NEList: cats.data.NonEmptyList.type = cats.data.NonEmptyList

  final type NonEmptyList[+A] = cats.data.NonEmptyList[A]
  final val NonEmptyList: cats.data.NonEmptyList.type = cats.data.NonEmptyList

  final type NESet[A] = cats.data.NonEmptySet[A]
  final val NESet: cats.data.NonEmptySet.type = cats.data.NonEmptySet

  final type NonEmptySet[A] = cats.data.NonEmptySet[A]
  final val NonEmptySet: cats.data.NonEmptySet.type = cats.data.NonEmptySet

  final type NonEmptyMap[K, +A] = cats.data.NonEmptyMap[K, A]
  final val NonEmptyMap: cats.data.NonEmptyMap.type = cats.data.NonEmptyMap

  final type NEMap[K, +A] = cats.data.NonEmptyMap[K, A]
  final val NEMap: cats.data.NonEmptyMap.type = cats.data.NonEmptyMap

  final type Chain[+A] = cats.data.Chain[A]
  final val Chain: cats.data.Chain.type = cats.data.Chain

  final type NonEmptyChain[+A] = cats.data.NonEmptyChain[A]
  final val NonEmptyChain: cats.data.NonEmptyChain.type = cats.data.NonEmptyChain

  //NE is much shorter than NonEmpty!
  final type NEChain[+A] = cats.data.NonEmptyChain[A]
  final val NEChain: cats.data.NonEmptyChain.type = cats.data.NonEmptyChain

  final type Kleisli[F[_], A, B] = cats.data.Kleisli[F, A, B]
  final val Kleisli: cats.data.Kleisli.type = cats.data.Kleisli

  final type ReaderT[F[_], A, B] = cats.data.ReaderT[F, A, B]
  final val ReaderT: cats.data.ReaderT.type = cats.data.ReaderT

  final type Reader[A, B] = cats.data.Reader[A, B]
  final val Reader: cats.data.Reader.type = cats.data.Reader

  //---------- cats-misc ---------------------

  final type Show[T] = cats.Show[T]
  final val Show: cats.Show.type = cats.Show

  //----------- cats-effect types -----------
  final type Sync[F[_]] = ce.Sync[F]
  final val Sync: ce.Sync.type = ce.Sync

  final type Async[F[_]] = ce.Async[F]
  final val Async: ce.Async.type = ce.Async

  final type Effect[F[_]] = ce.Effect[F]
  final val Effect: ce.Effect.type = ce.Effect

  final type Concurrent[F[_]] = ce.Concurrent[F]
  final val Concurrent: ce.Concurrent.type = ce.Concurrent

  final type ConcurrentEffect[F[_]] = ce.ConcurrentEffect[F]
  final val ConcurrentEffect: ce.ConcurrentEffect.type = ce.ConcurrentEffect

  final type Timer[F[_]] = ce.Timer[F]
  final val Timer: ce.Timer.type = ce.Timer

  final type ContextShift[F[_]] = ce.ContextShift[F]
  final val ContextShift: ce.ContextShift.type = ce.ContextShift

  final type Blocker = ce.Blocker
  final val Blocker: ce.Blocker.type = ce.Blocker

  final type CancelToken[F[_]] = ce.CancelToken[F]
  final type Fiber[F[_], A]    = ce.Fiber[F, A]
  final val Fiber: ce.Fiber.type = ce.Fiber

  final type SyncIO[+A] = ce.SyncIO[A]
  final val SyncIO: ce.SyncIO.type = ce.SyncIO

  final type IO[+A] = ce.IO[A]
  final val IO: ce.IO.type = ce.IO

  final type LiftIO[F[_]] = ce.LiftIO[F]
  final val LiftIO: ce.LiftIO.type = ce.LiftIO

  final type IOApp = ce.IOApp
  final val IOApp: ce.IOApp.type = ce.IOApp

  final type ExitCode = ce.ExitCode
  final val ExitCode: ce.ExitCode.type = ce.ExitCode

  final type Bracket[F[_], E] = ce.Bracket[F, E]
  final val Bracket: ce.Bracket.type = ce.Bracket

  final type BracketThrow[F[_]] = ce.BracketThrow[F]

  final type Resource[F[_], A] = ce.Resource[F, A]
  final val Resource: ce.Resource.type = ce.Resource

  final type ExitCase[+E] = ce.ExitCase[E]
  final val ExitCase: ce.ExitCase.type = ce.ExitCase

  //----------- cats-effect concurrent -----------

  final type Semaphore[F[_]] = ce.concurrent.Semaphore[F]
  final val Semaphore: ce.concurrent.Semaphore.type = ce.concurrent.Semaphore

  final type Deferred[F[_], A] = ce.concurrent.Deferred[F, A]
  final val Deferred: ce.concurrent.Deferred.type = ce.concurrent.Deferred

  final type TryableDeferred[F[_], A] = ce.concurrent.TryableDeferred[F, A]
  //intentionally has same companion object as Deferred.
  final val TryableDeferred: ce.concurrent.Deferred.type = ce.concurrent.Deferred

  @deprecated(
    "`MVar` is now deprecated in favour of a new generation `MVar2` with `tryRead` and `swap` support",
    "2.2.0",
  )
  final type MVar[F[_], A] = ce.concurrent.MVar[F, A]

  final type MVar2[F[_], A] = ce.concurrent.MVar2[F, A]
  final val MVar: ce.concurrent.MVar.type = ce.concurrent.MVar

  final type Ref[F[_], A] = ce.concurrent.Ref[F, A]
  final val Ref: ce.concurrent.Ref.type = ce.concurrent.Ref

  //----------- standard scala types -----------

  //brought in for easy pattern matching. Failure, and Success are used way too often
  //in way too many libraries, so we just alias the std Scala library ones
  final type Try[+A] = scala.util.Try[A]
  final val Try:        scala.util.Try.type     = scala.util.Try
  final val TryFailure: scala.util.Failure.type = scala.util.Failure
  final val TrySuccess: scala.util.Success.type = scala.util.Success

  final val NonFatal: scala.util.control.NonFatal.type = scala.util.control.NonFatal

  //----------- scala Future -----------
  final type Future[+A] = sc.Future[A]
  final val Future: sc.Future.type = sc.Future

  final type ExecutionContext = sc.ExecutionContext
  final val ExecutionContext: sc.ExecutionContext.type = sc.ExecutionContext

  final type ExecutionContextExecutor        = sc.ExecutionContextExecutor
  final type ExecutionContextExecutorService = sc.ExecutionContextExecutorService

  final val Await: sc.Await.type = sc.Await
}
