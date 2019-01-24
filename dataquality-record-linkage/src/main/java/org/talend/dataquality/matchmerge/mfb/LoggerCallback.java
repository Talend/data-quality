// ============================================================================
//
// Copyright (C) 2006-2016 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataquality.matchmerge.mfb;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.talend.dataquality.matchmerge.Attribute;
import org.talend.dataquality.matchmerge.MatchMergeAlgorithm;
import org.talend.dataquality.matchmerge.Record;

public class LoggerCallback implements MatchMergeAlgorithm.Callback {

    private static final Logger LOGGER = LoggerFactory.getLogger(MatchMergeAlgorithm.class);

    @Override
    public void onBeginRecord(Record record) {
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("-> Record #" + record);
        }
    }

    @Override
    public void onMatch(Record record1, Record record2, MatchResult matchResult) {
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("\t(+) Positive match: #" + record1.getId() + " <---> #" + record2.getId());
        }
        if (LOGGER.isDebugEnabled()) {
            StringBuilder messagesBuilder = new StringBuilder();
            int i = 0;
            for (MatchResult.Score score : matchResult.getScores()) {
                messagesBuilder.append("\t\t").append(score.algorithm.getComponentValue()).append("('").append(score.values[0])
                        .append("', '").append(score.values[1]).append("') = ").append(score.score).append(" (>= ")
                        .append(matchResult.getThresholds().get(i)).append(")");
                i++;
            }
            messagesBuilder.append('\n');
            messagesBuilder.append("\t\tconfidence: ").append(matchResult.getNormalizedConfidence()).append(")");
            LOGGER.debug(messagesBuilder.toString());
        }
    }

    @Override
    public void onNewMerge(Record record) {
        if (LOGGER.isInfoEnabled()) {
            if (record.getRelatedIds().size() > 1) {
                LOGGER.info("\t(+) New merge: #" + record.getId() + " (groups " + record.getRelatedIds().size() + " records).");
            } else {
                LOGGER.info("\t(+) New merge: #" + record.getId() + " (unique record).");
            }
        }
        if (LOGGER.isDebugEnabled()) {
            StringBuilder messageBuilder = new StringBuilder();
            for (Attribute attribute : record.getAttributes()) {
                messageBuilder.append("\t\t").append(attribute.getLabel()).append(": '").append(attribute.getValue()).append("'");
            }
            LOGGER.debug(messageBuilder.toString());
        }
    }

    @Override
    public void onRemoveMerge(Record record) {
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("\t(-) Removed merge: #" + record);
        }
    }

    @Override
    public void onDifferent(Record record1, Record record2, MatchResult matchResult) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("\t(-) Negative match: #" + record1.getId() + " <-/-> #" + record2.getId());
            StringBuilder messagesBuilder = new StringBuilder();
            int i = 0;
            for (MatchResult.Score score : matchResult.getScores()) {
                Float threshold = matchResult.getThresholds().get(i);
                String compareSymbol;
                if (score.score < threshold) {
                    compareSymbol = "<"; //$NON-NLS-1$
                } else {
                    compareSymbol = ">="; //$NON-NLS-1$
                }
                messagesBuilder.append("\t\t").append(score.algorithm.getComponentValue()).append("('").append(score.values[0])
                        .append("', '").append(score.values[1]).append("') = ").append(score.score).append(" (")
                        .append(compareSymbol).append(" ").append(threshold).append(")");
                i++;
            }
            messagesBuilder.append('\n');
            messagesBuilder.append("\t\tconfidence: ").append(matchResult.getNormalizedConfidence()).append(")");
            LOGGER.debug(messagesBuilder.toString());
        }
    }

    @Override
    public void onEndRecord(Record record) {
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("<- Record #" + record);
        }
    }

    @Override
    public boolean isInterrupted() {
        return false;
    }

    @Override
    public void onBeginProcessing() {
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Begin match & merge.");
        }
    }

    @Override
    public void onEndProcessing() {
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("End match & merge.");
        }
    }

    @Override
    public void onSynResult(Record newRecord, Record originalRecord, MatchResult matchResult) {
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Synchronize result between new merge record and original one");
        }

    }

}
