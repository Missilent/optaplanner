package org.optaplanner.constraint.streams.bavet.quad;

import java.util.List;
import java.util.Set;

import org.optaplanner.constraint.streams.bavet.BavetConstraintFactory;
import org.optaplanner.constraint.streams.bavet.common.BavetAbstractConstraintStream;
import org.optaplanner.constraint.streams.bavet.common.GroupNodeConstructor;
import org.optaplanner.constraint.streams.bavet.common.NodeBuildHelper;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.stream.ConstraintStream;

final class BavetQuadGroupBridgeQuadConstraintStream<Solution_, A, B, C, D, NewA, NewB, NewC, NewD>
        extends BavetAbstractQuadConstraintStream<Solution_, A, B, C, D> {

    protected final BavetAbstractQuadConstraintStream<Solution_, A, B, C, D> parent;
    protected BavetGroupQuadConstraintStream<Solution_, NewA, NewB, NewC, NewD> groupStream;
    private final GroupNodeConstructor<QuadTuple<NewA, NewB, NewC, NewD>> nodeConstructor;

    public BavetQuadGroupBridgeQuadConstraintStream(BavetConstraintFactory<Solution_> constraintFactory,
            BavetAbstractQuadConstraintStream<Solution_, A, B, C, D> parent,
            GroupNodeConstructor<QuadTuple<NewA, NewB, NewC, NewD>> nodeConstructor) {
        super(constraintFactory, parent.getRetrievalSemantics());
        this.parent = parent;
        this.nodeConstructor = nodeConstructor;
    }

    @Override
    public boolean guaranteesDistinct() {
        return true;
    }

    public void setGroupStream(BavetGroupQuadConstraintStream<Solution_, NewA, NewB, NewC, NewD> groupStream) {
        this.groupStream = groupStream;
    }

    // ************************************************************************
    // Node creation
    // ************************************************************************

    @Override
    public void collectActiveConstraintStreams(Set<BavetAbstractConstraintStream<Solution_>> constraintStreamSet) {
        parent.collectActiveConstraintStreams(constraintStreamSet);
        constraintStreamSet.add(this);
    }

    @Override
    public <Score_ extends Score<Score_>> void buildNode(NodeBuildHelper<Score_> buildHelper) {
        ConstraintStream parentTupleSource = parent.getTupleSource();
        List<? extends ConstraintStream> groupStreamChildList = groupStream.getChildStreamList();
        nodeConstructor.build(buildHelper, parentTupleSource, groupStream, groupStreamChildList, this, childStreamList);
    }

    @Override
    public ConstraintStream getTupleSource() {
        return parent.getTupleSource();
    }

}
