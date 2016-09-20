package se.lth.cs.srl.ml;

import uk.ac.ed.inf.srl.ml.liblinear.Label;
import uk.ac.ed.inf.srl.ml.Model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * We are an adapter wrapper around se.lth.cs.srl.ml.Model for instances
 * where you need a uk.ac.ed.inf.srl.ml.Model
 */
public class UkModelAdapter implements Model {
    protected se.lth.cs.srl.ml.Model wrapped;

    public UkModelAdapter(se.lth.cs.srl.ml.Model wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public List<Label> classifyProb(Collection<Integer> indices, Map<Integer, Double> nonbinFeats) {
        List<se.lth.cs.srl.ml.liblinear.Label> wrappedLabels = wrapped.classifyProb(indices, nonbinFeats);
        List<Label> results = new ArrayList<Label>(wrappedLabels.size());
        for (se.lth.cs.srl.ml.liblinear.Label one: wrappedLabels) {
            results.add(new Label(one.getLabel(), one.getProb()));
        }
        return null;
    }

    @Override
    public Integer classify(Collection<Integer> indices, Map<Integer, Double> nonbinFeats) {
        return wrapped.classify(indices, nonbinFeats);
    }
}
