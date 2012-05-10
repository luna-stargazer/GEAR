package family.pedigree.genotype;


import java.util.TreeMap;

import parameter.Parameter;
import util.NewIt;
/**
*
* @author Guo-Bo Chen, chenguobo@gmail.com
*/
public class GenoSet {

    TreeMap<String, Integer> parentsGenoMap;
    TreeMap<String, Integer> childrenGenoMap;
    TreeMap<String, Integer> fullparentsGenoMap;
    TreeMap<String, Integer> fullchildrenGenoMap;
    private int index;
    private int ungenotypedParents = 0;
    private int genotypedParents = 0;
    private int ungenotypedChildren = 0;
    private int genotypedChildren = 0;

    public GenoSet(TreeMap<String, Integer> parents, TreeMap<String, Integer> children, int index) {
        fullparentsGenoMap = NewIt.newTreeMap();
        fullchildrenGenoMap = NewIt.newTreeMap();
        parentsGenoMap = parents;
        childrenGenoMap = children;
        this.index = index;
        initial();
    }

    /**
     * kick the untyped individuals from the genotypeMap;
     */
    private void initial() {
        for (String p: parentsGenoMap.keySet()) {
            genotypedParents += parentsGenoMap.get(p).intValue();
        }

        if (parentsGenoMap.containsKey(Parameter.missingGenotype)) {
            ungenotypedParents = ((Integer) parentsGenoMap.get(Parameter.missingGenotype)).intValue();
            parentsGenoMap.remove(Parameter.missingGenotype);
        }
        genotypedParents -= ungenotypedParents;

        for (String c:childrenGenoMap.keySet()) {
            genotypedChildren += childrenGenoMap.get(c).intValue();
        }

        if (childrenGenoMap.containsKey(Parameter.missingGenotype)) {
            ungenotypedChildren = childrenGenoMap.get(Parameter.missingGenotype).intValue();
            childrenGenoMap.remove(Parameter.missingGenotype);
        }
        genotypedChildren -= ungenotypedChildren;
    }

    public TreeMap<String, Integer> getfullparentsGenoMap() {
        return fullparentsGenoMap;
    }

    /**
     * return typed parents genotypes'map
     * 
     * @return
     */
    public TreeMap<String, Integer> getparentsGenoMap() {
        return parentsGenoMap;
    }

    public TreeMap<String, Integer> getfullchildrenGenoMap() {
        return fullchildrenGenoMap;
    }

    /**
     * return typed children genotypes'map
     * 
     * @return
     */
    public TreeMap<String, Integer> getchildrenGenoMap() {
        return childrenGenoMap;
    }

    public int getNumParents() {
        return genotypedParents + ungenotypedParents;
    }

    public int getNumUntypedParents() {
        return ungenotypedParents;
    }

    public int getNumTypedParents() {
        return genotypedParents;
    }

    public int getNumChildren() {
        return genotypedChildren + ungenotypedChildren;
    }

    public int getNumUntypedChildren() {
        return ungenotypedChildren;
    }

    public int getNumTypedChildren() {
        return genotypedChildren;
    }

    public int getIndex() {
        return index;
    }
}