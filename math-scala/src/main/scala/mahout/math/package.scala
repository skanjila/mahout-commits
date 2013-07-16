package mahout

import org.apache.mahout.math._
import scala.Tuple2
import org.apache.mahout.math.solver.EigenDecomposition

/**
 * Mahout matrices and vectors' scala syntactic sugar
 */
package object math {

  implicit def vector2vectorOps(v: Vector) = new VectorOps(v)

  implicit def matrix2matrixOps(m: Matrix) = new MatrixOps(m)

  implicit def seq2Vector(s: TraversableOnce[AnyVal]) =
    new DenseVector(s.map(_.asInstanceOf[Number].doubleValue()).toArray)

  implicit def tuple2TravOnce2svec[V <: AnyVal](sdata: TraversableOnce[(Int, V)]) = svec(sdata)

  implicit def t1vec(s: Tuple1[AnyVal]): Vector = prod2Vec(s)

  implicit def t2vec(s: Tuple2[AnyVal, AnyVal]): Vector = prod2Vec(s)

  implicit def t3vec(s: Tuple3[AnyVal, AnyVal, AnyVal]): Vector = prod2Vec(s)

  implicit def t4vec(s: Tuple4[AnyVal, AnyVal, AnyVal, AnyVal]): Vector = prod2Vec(s)

  implicit def t5vec(s: Tuple5[AnyVal, AnyVal, AnyVal, AnyVal, AnyVal]): Vector = prod2Vec(s)

  implicit def t6vec(s: Tuple6[AnyVal, AnyVal, AnyVal, AnyVal, AnyVal, AnyVal]): Vector = prod2Vec(s)

  implicit def t7vec(s: Tuple7[AnyVal, AnyVal, AnyVal, AnyVal, AnyVal, AnyVal, AnyVal]): Vector = prod2Vec(s)

  implicit def t8vec(s: Tuple8[AnyVal, AnyVal, AnyVal, AnyVal, AnyVal, AnyVal, AnyVal, AnyVal]): Vector = prod2Vec(s)

  implicit def t9vec(s: Tuple9[AnyVal, AnyVal, AnyVal, AnyVal, AnyVal, AnyVal, AnyVal, AnyVal, AnyVal]): Vector =
    prod2Vec(s)

  implicit def t10vec(s: Tuple10[AnyVal, AnyVal, AnyVal, AnyVal, AnyVal, AnyVal, AnyVal, AnyVal, AnyVal, AnyVal])
  : Vector = prod2Vec(s)

  implicit def t11vec(s: Tuple11[AnyVal, AnyVal, AnyVal, AnyVal, AnyVal, AnyVal, AnyVal, AnyVal, AnyVal, AnyVal
    , AnyVal])
  : Vector = prod2Vec(s)

  implicit def t12vec(s: Tuple12[AnyVal, AnyVal, AnyVal, AnyVal, AnyVal, AnyVal, AnyVal, AnyVal, AnyVal, AnyVal
    , AnyVal, AnyVal])
  : Vector = prod2Vec(s)

  implicit def t13vec(s: Tuple13[AnyVal, AnyVal, AnyVal, AnyVal, AnyVal, AnyVal, AnyVal, AnyVal, AnyVal, AnyVal
    , AnyVal, AnyVal, AnyVal])
  : Vector = prod2Vec(s)

  implicit def t14vec(s: Tuple14[AnyVal, AnyVal, AnyVal, AnyVal, AnyVal, AnyVal, AnyVal, AnyVal, AnyVal, AnyVal
    , AnyVal, AnyVal, AnyVal, AnyVal])
  : Vector = prod2Vec(s)

  implicit def t15vec(s: Tuple15[AnyVal, AnyVal, AnyVal, AnyVal, AnyVal, AnyVal, AnyVal, AnyVal, AnyVal, AnyVal
    , AnyVal, AnyVal, AnyVal, AnyVal, AnyVal])
  : Vector = prod2Vec(s)

  implicit def t16vec(s: Tuple16[AnyVal, AnyVal, AnyVal, AnyVal, AnyVal, AnyVal, AnyVal, AnyVal, AnyVal, AnyVal
    , AnyVal, AnyVal, AnyVal, AnyVal, AnyVal, AnyVal])
  : Vector = prod2Vec(s)

  implicit def t17vec(s: Tuple17[AnyVal, AnyVal, AnyVal, AnyVal, AnyVal, AnyVal, AnyVal, AnyVal, AnyVal, AnyVal
    , AnyVal, AnyVal, AnyVal, AnyVal, AnyVal, AnyVal, AnyVal])
  : Vector = prod2Vec(s)

  implicit def t18vec(s: Tuple18[AnyVal, AnyVal, AnyVal, AnyVal, AnyVal, AnyVal, AnyVal, AnyVal, AnyVal, AnyVal
    , AnyVal, AnyVal, AnyVal, AnyVal, AnyVal, AnyVal, AnyVal, AnyVal])
  : Vector = prod2Vec(s)

  implicit def t19vec(s: Tuple19[AnyVal, AnyVal, AnyVal, AnyVal, AnyVal, AnyVal, AnyVal, AnyVal, AnyVal, AnyVal
    , AnyVal, AnyVal, AnyVal, AnyVal, AnyVal, AnyVal, AnyVal, AnyVal, AnyVal])
  : Vector = prod2Vec(s)

  implicit def t20vec(s: Tuple20[AnyVal, AnyVal, AnyVal, AnyVal, AnyVal, AnyVal, AnyVal, AnyVal, AnyVal, AnyVal
    , AnyVal, AnyVal, AnyVal, AnyVal, AnyVal, AnyVal, AnyVal, AnyVal, AnyVal, AnyVal])
  : Vector = prod2Vec(s)

  implicit def t21vec(s: Tuple21[AnyVal, AnyVal, AnyVal, AnyVal, AnyVal, AnyVal, AnyVal, AnyVal, AnyVal, AnyVal
    , AnyVal, AnyVal, AnyVal, AnyVal, AnyVal, AnyVal, AnyVal, AnyVal, AnyVal, AnyVal, AnyVal])
  : Vector = prod2Vec(s)

  implicit def t22vec(s: Tuple22[AnyVal, AnyVal, AnyVal, AnyVal, AnyVal, AnyVal, AnyVal, AnyVal, AnyVal, AnyVal
    , AnyVal, AnyVal, AnyVal, AnyVal, AnyVal, AnyVal, AnyVal, AnyVal, AnyVal, AnyVal, AnyVal, AnyVal])
  : Vector = prod2Vec(s)


  def prod2Vec(s: Product) = new DenseVector(s.productIterator.
    map(_.asInstanceOf[Number].doubleValue()).toArray)

  def diagv(v: Vector): DiagonalMatrix = new DiagonalMatrix(v)

  def diag(v: Double, size: Int): DiagonalMatrix = new DiagonalMatrix(v, size)

  /**
   * Create dense matrix out of inline arguments -- rows -- which can be tuples,
   * iterables of Double, or just single Number (for columnar vectors)
   * @param rows
   * @tparam R
   * @return
   */
  def dense[R](rows: R*): DenseMatrix = {
    val data = for (r <- rows) yield {
      r match {
        case n: Number => Array(n.doubleValue())
        case t: Product => t.productIterator.map(_.asInstanceOf[Number].doubleValue()).toArray
        case t: Array[Double] => t
        case t: Iterable[Double] => t.toArray
        case t: Array[Array[Double]] => if (rows.size == 1)
          return new DenseMatrix(t)
        else
          throw new IllegalArgumentException(
            "double[][] data parameter can be the only argumentn for dense()")
        case _ => throw new IllegalArgumentException("unsupported type in the inline Matrix initializer")
      }
    }
    new DenseMatrix(data.toArray)
  }

  /**
   * Default initializes are always row-wise.
   * create a sparse,
   * e.g.
   * m = sparse(
   * (0,5)::(9,3)::Nil,
   * (2,3.5)::(7,8)::Nil
   * )
   *
   * @param rows
   * @return
   */

  def sparse(rows: Vector*): SparseRowMatrix = {
    val nrow = rows.size
    val ncol = rows.map(_.size()).max
    val m = new SparseRowMatrix(nrow, ncol)
    m := rows
    m

  }

  /**
   * create a sparse vector out of list of tuple2's
   * @param sdata
   * @return
   */
  def svec(sdata: TraversableOnce[(Int, AnyVal)]) = {
    val cardinality = if (sdata.size > 0) sdata.map(_._1).max + 1 else 0
    val initialCapacity = sdata.size
    val sv = new RandomAccessSparseVector(cardinality, initialCapacity)
    sdata.foreach(t => sv.setQuick(t._1, t._2.asInstanceOf[Number].doubleValue()))
    sv
  }

  def dvec(ddata: TraversableOnce[Double]) = new DenseVector(ddata.toArray)

  def chol(m: Matrix, typ: Boolean = false) = new CholeskyDecomposition(m, typ)

  /**
   * computes SVD
   * @param m svd input
   * @return (U,V, singular-values-vector)
   */
  def svd(m: Matrix) = {
    val svdObj = new SingularValueDecomposition(m)
    (svdObj.getU, svdObj.getV, new DenseVector(svdObj.getSingularValues))
  }

  /**
   * Computes Eigendecomposition of a symmetric matrix
   * @param m symmetric input matrix
   * @return (V, eigen-values-vector)
   */
  def eigen(m: Matrix) = {
    val ed = new EigenDecomposition(m, true)
    (ed.getV, ed.getRealEigenvalues)
  }


  /**
   * More general version of eigen decomposition
   * @param m
   * @param symmetric
   * @return (V, eigenvalues-real-vector, eigenvalues-imaginary-vector)
   */
  def eigen(m: Matrix, symmetric: Boolean) {
    val ed = new EigenDecomposition(m, true)
    (ed.getV, ed.getRealEigenvalues, ed.getImagEigenvalues)
  }

  def ::() = Range(0, 0)

}
