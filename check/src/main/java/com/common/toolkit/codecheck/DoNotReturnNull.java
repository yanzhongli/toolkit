package com.common.toolkit.codecheck;

import static com.google.errorprone.BugPattern.Category.JDK;
import static com.google.errorprone.BugPattern.LinkType.CUSTOM;
import static com.google.errorprone.BugPattern.SeverityLevel.ERROR;
import static com.google.errorprone.BugPattern.SeverityLevel.SUGGESTION;
import static com.google.errorprone.matchers.Matchers.contains;
import static com.google.errorprone.matchers.Matchers.hasAnnotation;
import static com.sun.source.tree.Tree.Kind.NULL_LITERAL;

import com.google.auto.service.AutoService;
import com.google.errorprone.BugPattern;
import com.google.errorprone.VisitorState;
import com.google.errorprone.bugpatterns.BugChecker;
import com.google.errorprone.bugpatterns.BugChecker.MethodTreeMatcher;
import com.google.errorprone.matchers.Description;
import com.google.errorprone.matchers.Matcher;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.ReturnTree;
import com.sun.source.tree.Tree;
import javax.annotation.Nullable;

/**
 * Bug checker to detect usage of {@code return null;}.
 *
 * just suggestion
 */
@AutoService(BugChecker.class)
@BugPattern(
    name = "DoNotReturnNull",
    summary = "Do not return null.",
    category = JDK,
    severity = SUGGESTION,
    linkType = CUSTOM,
    link = "example.com/codecheck/DoNotReturnNull"
)
public class DoNotReturnNull extends BugChecker implements MethodTreeMatcher {

  private static final Matcher<Tree> HAS_NULLABLE_ANNOTATION =
      hasAnnotation(Nullable.class.getCanonicalName());
  private static final Matcher<Tree> RETURN_NULL = new ReturnNullMatcher();
  private static final Matcher<Tree> CONTAINS_RETURN_NULL = contains(RETURN_NULL);


  @Override
  public Description matchMethod(MethodTree tree, VisitorState state) {
    if (HAS_NULLABLE_ANNOTATION.matches(tree, state)) {
      //除去抽象方法
      if (tree.getBody() != null && !CONTAINS_RETURN_NULL
          .matches(tree.getBody(), state)) {
        return Description.NO_MATCH;
      }
    }
    return describeMatch(tree);
  }

  private static class ReturnNullMatcher implements Matcher<Tree> {

    @Override
    public boolean matches(Tree tree, VisitorState state) {
      if (tree instanceof ReturnTree) {
        return ((ReturnTree) tree).getExpression().getKind() == NULL_LITERAL;
      }
      return false;
    }
  }
}