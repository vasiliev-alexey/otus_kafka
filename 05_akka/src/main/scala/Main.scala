import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.scaladsl.{Broadcast, Flow, GraphDSL, RunnableGraph, Sink, Source, ZipWith}
import akka.stream.{ClosedShape, Graph}

object Main {
  implicit val system: ActorSystem = ActorSystem("hw_5")

  val graph: Graph[ClosedShape.type, NotUsed] = GraphDSL.create() { implicit builder =>
    import GraphDSL.Implicits._

    val input = builder.add(Source(1 to 5))
    val multOper10 = builder.add(Flow[Int].map(x => x * 10))
    val multOper2 = builder.add(Flow[Int].map(x => x * 2))
    val multOper3 = builder.add(Flow[Int].map(x => x * 3))
    val outputStream = builder.add(Sink.foreach(println))
    val broadcast = builder.add(Broadcast[Int](3))
    val zipStream = builder.add(ZipWith[Int, Int, Int, (Int, Int, Int)]((a, b, c) => (a, b, c)))

    input ~> broadcast
    broadcast.out(0) ~> multOper10 ~> zipStream.in0
    broadcast.out(1) ~> multOper2 ~> zipStream.in1
    broadcast.out(2) ~> multOper3 ~> zipStream.in2
    zipStream.out ~> outputStream

    ClosedShape
  }

  def main(args: Array[String]): Unit = {
    RunnableGraph.fromGraph(graph).run()
  }
}