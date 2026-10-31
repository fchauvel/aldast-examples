// A suffix tree helps us find where a fragment occurs in a text.
//
// The idea is that a fragment occurs in a text if, and only if, it is the
// beginning of one of the text's suffixes. For example, "nan" occurs in
// "banana" because it is the beginning of the suffix "nana". So, we store
// all the suffixes of the text in a prefix tree, and we search for the
// fragment by prefix.
//
// The code favours clarity over efficiency.

class SuffixTree(text: String):

  private val suffixes = PrefixTree()

  // All the suffixes of the text, from the whole text to the empty one
  for start <- 0 to text.length do
    suffixes.add(text.drop(start))

  // Where does the fragment start in the text? The first character has
  // position 0. There is no position when the fragment does not occur.
  def find(fragment: String): Seq[Int] =
    val matchingSuffixes = suffixes.prefixSearch(fragment)
    val positions = matchingSuffixes.map(suffix => text.length - suffix.length)
    positions.toSeq.sorted
