/*
 * Copyright 1999-2017 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.druid.sql.ast.statement;

import com.alibaba.druid.sql.ast.SQLParameter;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.SQLStatementImpl;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

import java.util.ArrayList;
import java.util.List;

public class SQLBlockStatement extends SQLStatementImpl {
    private boolean isDollarQuoted;
    private String dollarQuoteTagName;
    private String labelName;
    private String endLabel;
    private List<SQLParameter> parameters = new ArrayList<SQLParameter>();
    private List<SQLStatement> statementList = new ArrayList<SQLStatement>();
    public SQLStatement exception;
    private boolean endOfCommit;
    private boolean haveBeginEnd;
    private String language;

    public SQLBlockStatement() {
        haveBeginEnd = true;
    }

    public List<SQLStatement> getStatementList() {
        return statementList;
    }

    public void setStatementList(List<SQLStatement> statementList) {
        this.statementList = statementList;
    }

    public boolean isDollarQuoted() {
        return isDollarQuoted;
    }

    public void setIsDollarQuoted(boolean isDollarQuoted) {
        this.isDollarQuoted = isDollarQuoted;
    }

    public String getDollarQuoteTagName() {
        return dollarQuoteTagName;
    }

    public void setDollarQuoteTagName(String dollarQuoteTagName) {
        this.dollarQuoteTagName = dollarQuoteTagName;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getLabelName() {
        return labelName;
    }

    public void setLabelName(String labelName) {
        this.labelName = labelName;
    }

    public boolean isHaveBeginEnd() {
        return haveBeginEnd;
    }

    public void setHaveBeginEnd(boolean haveBeginEnd) {
        this.haveBeginEnd = haveBeginEnd;
    }

    @Override
    public void accept0(SQLASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, parameters);
            acceptChild(visitor, statementList);
            acceptChild(visitor, exception);
        }
        visitor.endVisit(this);
    }

    public List<SQLParameter> getParameters() {
        return parameters;
    }

    public void setParameters(List<SQLParameter> parameters) {
        this.parameters = parameters;
    }

    public SQLStatement getException() {
        return exception;
    }

    public void setException(SQLStatement exception) {
        if (exception != null) {
            exception.setParent(this);
        }
        this.exception = exception;
    }

    public String getEndLabel() {
        return endLabel;
    }

    public void setEndLabel(String endLabel) {
        this.endLabel = endLabel;
    }

    public SQLBlockStatement clone() {
        SQLBlockStatement x = new SQLBlockStatement();
        x.labelName = labelName;
        x.endLabel = endLabel;

        for (SQLParameter p : parameters) {
            SQLParameter p2 = p.clone();
            p2.setParent(x);
            x.parameters.add(p2);
        }

        for (SQLStatement stmt : statementList) {
            SQLStatement stmt2 = stmt.clone();
            stmt2.setParent(x);
            x.statementList.add(stmt2);
        }

        if (exception != null) {
            x.setException(exception.clone());
        }

        return x;
    }

    public SQLParameter findParameter(long hash) {
        for (SQLParameter param : this.parameters) {
            if (param.getName().nameHashCode64() == hash) {
                return param;
            }
        }

        return null;
    }

    public boolean isEndOfCommit() {
        return endOfCommit;
    }

    public void setEndOfCommit(boolean value) {
        this.endOfCommit = value;
    }

    public boolean replace(SQLStatement cmp, SQLStatement target) {
        for (int i = 0; i < statementList.size(); i++) {
            if (statementList.get(i) == cmp) {
                statementList.set(i, target);
                return true;
            }
        }

        return false;
    }
}
