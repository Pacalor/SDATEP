/* 
 * The MIT License
 *
 * Copyright 2018 Ángel Miguel García Vico.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package es.ujaen.simidat.agvico.moa.subgroupdiscovery.qualitymeasures;

import es.ujaen.simidat.agvico.org.core.exceptions.InvalidMeasureComparisonException;
import es.ujaen.simidat.agvico.org.core.exceptions.InvalidRangeInMeasureException;
import moa.core.ObjectRepository;
import moa.tasks.TaskMonitor;

/**
 *
 * @author agvico
 */
public final class SuppDiff extends QualityMeasure {

    public SuppDiff() {
        super.name = "Support Difference";
        super.short_name = "SuppDiff";
        super.value = 0.0;
    }

    @Override
    public void getDescription(StringBuilder arg0, int arg1) {
    }

    @Override
    public double calculateValue(ContingencyTable t) {
        table = t;
        try {
            TPR tpr = new TPR();
            FPR fpr = new FPR();

            tpr.calculateValue(t);
            fpr.calculateValue(t);
            tpr.validate();
            fpr.validate();

            setValue(tpr.value - fpr.value);
        } catch (InvalidRangeInMeasureException ex) {
            ex.showAndExit(this);
        }
        return value;
    }

    @Override
    public void validate() throws InvalidRangeInMeasureException {
        if (!(value >= -1.0 && value <= 1.0) || Double.isNaN(value)) {
            throw new InvalidRangeInMeasureException(this);
        }
    }

    @Override
    public QualityMeasure clone() {
        SuppDiff a = new SuppDiff();
        a.name = this.name;
        a.setValue(this.value);

        return a;
    }

    @Override
    protected void prepareForUseImpl(TaskMonitor tm, ObjectRepository or) {
    }

    @Override
    public int compareTo(QualityMeasure o) {
        try {
            if (!(o instanceof SuppDiff)) {
                throw new InvalidMeasureComparisonException(this, o);
            }

            if (this.value < o.value) {
                return -1;
            } else if (this.value > o.value) {
                return 1;
            } else {
                return 0;
            }
        } catch (InvalidMeasureComparisonException ex) {
            ex.showAndExit(this);
        }
        return 0;
    }

}
