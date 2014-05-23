package edu.cmu.cs.lti.ark.diversity.utils;

import java.util.List;

import com.google.common.collect.Lists;

public class Conll {

    public class ConllElement {
        private int position;
        private String token;
        private String lemma;
        private String coarsePosTag;
        private String posTag;
        private int parent;
        final private int goldParent; // gold standard parent
        private String depLabel;

        ConllElement(
                int position, String token, String lemma, String coarsePosTag, String posTag,
                int goldParent) {
            this.position = position;
            this.token = token;
            this.lemma = lemma;
            this.coarsePosTag = coarsePosTag;
            this.posTag = posTag;
            this.goldParent = goldParent;
        }

        public int getPosition() {
            return position;
        }

        public String getToken() {
            return token;
        }

        public String getLemma() {
            return lemma;
        }

        public String getCoarsePosTag() {
            return coarsePosTag;
        }

        public String getPosTag() {
            return posTag;
        }

        public int getParent() {
            return parent;
        }

        public void setParent(int parent) {
            this.parent = parent;
        }

        public int getGoldParent() {
            return goldParent;
        }

        public String getDepLabel() {
            return depLabel;
        }

        public void setDepLabel(String depLabel) {
            this.depLabel = depLabel;
        }

    }

    private List<ConllElement> elements;

    public Conll(List<String> lines) {
        List<ConllElement> elements = Lists.newArrayList();
        for (String line : lines) {
            String ele[] = line.split("\t");
            elements.add(new ConllElement(Integer.parseInt(ele[0]),
                    ele[1],
                    ele[2],
                    ele[3],
                    ele[4],
                    Integer.parseInt(ele[6])));
        }
        this.elements = elements;
    }
    public List<ConllElement> getElements() {
        return elements;
    }

    public void setElements(List<ConllElement> elements) {
        this.elements = elements;
    }

}
