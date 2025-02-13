package org.optaplanner.core.impl.heuristic.selector.move.generic.list.kopt;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.optaplanner.core.impl.testdata.util.PlannerTestUtils.mockRebasingScoreDirector;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.api.score.director.ScoreDirector;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.core.impl.domain.variable.descriptor.ListVariableDescriptor;
import org.optaplanner.core.impl.domain.variable.index.IndexVariableDemand;
import org.optaplanner.core.impl.domain.variable.index.IndexVariableListener;
import org.optaplanner.core.impl.domain.variable.index.IndexVariableSupply;
import org.optaplanner.core.impl.heuristic.move.AbstractMove;
import org.optaplanner.core.impl.score.director.InnerScoreDirector;
import org.optaplanner.core.impl.testdata.domain.list.TestdataListEntity;
import org.optaplanner.core.impl.testdata.domain.list.TestdataListSolution;
import org.optaplanner.core.impl.testdata.domain.list.TestdataListValue;
import org.optaplanner.core.impl.testdata.util.PlannerTestUtils;

class TwoOptListMoveTest {
    private final SolutionDescriptor<TestdataListSolution> solutionDescriptor = TestdataListSolution.buildSolutionDescriptor();
    private final InnerScoreDirector<TestdataListSolution, ?> scoreDirector =
            PlannerTestUtils.mockScoreDirector(solutionDescriptor);
    private final ListVariableDescriptor<TestdataListSolution> variableDescriptor =
            solutionDescriptor.getListVariableDescriptors().get(0);

    @Test
    void doMove() {
        IndexVariableSupply indexVariableSupply =
                scoreDirector.getSupplyManager().demand(new IndexVariableDemand<>(variableDescriptor));
        IndexVariableListener indexVariableListener = (IndexVariableListener) indexVariableSupply;
        TestdataListValue v1 = new TestdataListValue("1");
        TestdataListValue v2 = new TestdataListValue("2");
        TestdataListValue v3 = new TestdataListValue("3");
        TestdataListValue v4 = new TestdataListValue("4");
        TestdataListValue v5 = new TestdataListValue("5");
        TestdataListValue v6 = new TestdataListValue("6");
        TestdataListValue v7 = new TestdataListValue("7");
        TestdataListValue v8 = new TestdataListValue("8");
        TestdataListEntity e1 = new TestdataListEntity("e1", new ArrayList<>(
                List.of(v1, v2, v5, v4, v3, v6, v7, v8)));

        indexVariableListener.afterListVariableChanged(scoreDirector, e1, 0, 8);

        // 2-Opt((v2, v5), (v3, v6))
        TwoOptListMove<TestdataListSolution> move = new TwoOptListMove<>(variableDescriptor, indexVariableSupply,
                e1, v5, v6);
        AbstractMove<TestdataListSolution> undoMove = move.doMove(scoreDirector);
        assertThat(e1.getValueList()).containsExactly(v1, v2, v3, v4, v5, v6, v7, v8);

        verify(scoreDirector).beforeListVariableChanged(variableDescriptor, e1, 2, 5);
        verify(scoreDirector).afterListVariableChanged(variableDescriptor, e1, 2, 5);
        verify(scoreDirector).triggerVariableListeners();

        undoMove.doMoveOnly(scoreDirector);
        assertThat(e1.getValueList()).containsExactly(v1, v2, v5, v4, v3, v6, v7, v8);
    }

    @Test
    void doMoveSecondEndsBeforeFirst() {
        IndexVariableSupply indexVariableSupply =
                scoreDirector.getSupplyManager().demand(new IndexVariableDemand<>(variableDescriptor));
        IndexVariableListener indexVariableListener = (IndexVariableListener) indexVariableSupply;
        TestdataListValue v1 = new TestdataListValue("1");
        TestdataListValue v2 = new TestdataListValue("2");
        TestdataListValue v3 = new TestdataListValue("3");
        TestdataListValue v4 = new TestdataListValue("4");
        TestdataListValue v5 = new TestdataListValue("5");
        TestdataListValue v6 = new TestdataListValue("6");
        TestdataListValue v7 = new TestdataListValue("7");
        TestdataListValue v8 = new TestdataListValue("8");
        TestdataListEntity e1 = new TestdataListEntity("e1", new ArrayList<>(
                List.of(v8, v7, v3, v4, v5, v6, v2, v1)));

        indexVariableListener.afterListVariableChanged(scoreDirector, e1, 0, 8);

        // 2-Opt((v6, v2), (v7, v3))
        TwoOptListMove<TestdataListSolution> move = new TwoOptListMove<>(variableDescriptor, indexVariableSupply,
                e1, v2, v3);
        AbstractMove<TestdataListSolution> undoMove = move.doMove(scoreDirector);
        assertThat(e1.getValueList()).containsExactly(v8, v1, v2, v3, v4, v5, v6, v7);

        verify(scoreDirector).beforeListVariableChanged(variableDescriptor, e1, 0, 8);
        verify(scoreDirector).afterListVariableChanged(variableDescriptor, e1, 0, 8);
        verify(scoreDirector).triggerVariableListeners();

        undoMove.doMoveOnly(scoreDirector);
        assertThat(e1.getValueList()).containsExactly(v8, v7, v3, v4, v5, v6, v2, v1);
    }

    @Test
    void doMoveSecondEndsBeforeFirstUnbalanced() {
        IndexVariableSupply indexVariableSupply =
                scoreDirector.getSupplyManager().demand(new IndexVariableDemand<>(variableDescriptor));
        IndexVariableListener indexVariableListener = (IndexVariableListener) indexVariableSupply;
        TestdataListValue v1 = new TestdataListValue("1");
        TestdataListValue v2 = new TestdataListValue("2");
        TestdataListValue v3 = new TestdataListValue("3");
        TestdataListValue v4 = new TestdataListValue("4");
        TestdataListValue v5 = new TestdataListValue("5");
        TestdataListValue v6 = new TestdataListValue("6");
        TestdataListValue v7 = new TestdataListValue("7");
        TestdataListEntity e1 = new TestdataListEntity("e1", new ArrayList<>(
                List.of(v5, v2, v3, v4, v1, v7, v6)));

        indexVariableListener.afterListVariableChanged(scoreDirector, e1, 0, 8);

        // 2-Opt((v4, v1), (v5, v2))
        TwoOptListMove<TestdataListSolution> move = new TwoOptListMove<>(variableDescriptor, indexVariableSupply,
                e1, v1, v2);
        AbstractMove<TestdataListSolution> undoMove = move.doMove(scoreDirector);
        assertThat(e1.getValueList()).containsExactly(v5, v6, v7, v1, v2, v3, v4);

        verify(scoreDirector).beforeListVariableChanged(variableDescriptor, e1, 0, 7);
        verify(scoreDirector).afterListVariableChanged(variableDescriptor, e1, 0, 7);
        verify(scoreDirector).triggerVariableListeners();

        undoMove.doMoveOnly(scoreDirector);
        assertThat(e1.getValueList()).containsExactly(v5, v2, v3, v4, v1, v7, v6);
    }

    @Test
    void doMoveFirstEndsBeforeSecondUnbalanced() {
        IndexVariableSupply indexVariableSupply =
                scoreDirector.getSupplyManager().demand(new IndexVariableDemand<>(variableDescriptor));
        IndexVariableListener indexVariableListener = (IndexVariableListener) indexVariableSupply;
        TestdataListValue v1 = new TestdataListValue("1");
        TestdataListValue v2 = new TestdataListValue("2");
        TestdataListValue v3 = new TestdataListValue("3");
        TestdataListValue v4 = new TestdataListValue("4");
        TestdataListValue v5 = new TestdataListValue("5");
        TestdataListValue v6 = new TestdataListValue("6");
        TestdataListValue v7 = new TestdataListValue("7");
        TestdataListEntity e1 = new TestdataListEntity("e1", new ArrayList<>(
                List.of(v2, v1, v7, v4, v5, v6, v3)));

        indexVariableListener.afterListVariableChanged(scoreDirector, e1, 0, 8);

        // 2-Opt((v4, v1), (v5, v2))
        TwoOptListMove<TestdataListSolution> move = new TwoOptListMove<>(variableDescriptor, indexVariableSupply,
                e1, v3, v4);
        AbstractMove<TestdataListSolution> undoMove = move.doMove(scoreDirector);
        assertThat(e1.getValueList()).containsExactly(v2, v3, v4, v5, v6, v7, v1);

        verify(scoreDirector).beforeListVariableChanged(variableDescriptor, e1, 0, 7);
        verify(scoreDirector).afterListVariableChanged(variableDescriptor, e1, 0, 7);
        verify(scoreDirector).triggerVariableListeners();

        undoMove.doMoveOnly(scoreDirector);
        assertThat(e1.getValueList()).containsExactly(v2, v1, v7, v4, v5, v6, v3);
    }

    @Test
    void rebase() {
        TestdataListValue v1 = new TestdataListValue("1");
        TestdataListValue v2 = new TestdataListValue("2");
        TestdataListEntity e1 = new TestdataListEntity("e1");

        TestdataListValue destinationV1 = new TestdataListValue("1");
        TestdataListValue destinationV2 = new TestdataListValue("2");
        TestdataListEntity destinationE1 = new TestdataListEntity("e1");

        ScoreDirector<TestdataListSolution> destinationScoreDirector = mockRebasingScoreDirector(
                variableDescriptor.getEntityDescriptor().getSolutionDescriptor(), new Object[][] {
                        { v1, destinationV1 },
                        { v2, destinationV2 },
                        { e1, destinationE1 },
                });

        IndexVariableSupply indexVariableSupply =
                scoreDirector.getSupplyManager().demand(new IndexVariableDemand<>(variableDescriptor));

        assertSameProperties(
                destinationE1, destinationV2, destinationV1, destinationV1, destinationV2,
                new TwoOptListMove<>(variableDescriptor, indexVariableSupply, e1, v2, v1, v1, v2)
                        .rebase(destinationScoreDirector));
    }

    static void assertSameProperties(
            Object destinationEntity, Object startV1, Object destinationV1, Object startV2, Object destinationV2,
            TwoOptListMove<?> move) {
        assertThat(move.getEntity()).isSameAs(destinationEntity);
        assertThat(move.getFirstEdgeStartpoint()).isSameAs(startV1);
        assertThat(move.getFirstEdgeEndpoint()).isSameAs(destinationV1);
        assertThat(move.getSecondEdgeStartpoint()).isEqualTo(startV2);
        assertThat(move.getSecondEdgeEndpoint()).isSameAs(destinationV2);
    }
}
