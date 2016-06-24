package io.cloudslang.content.xml.services;

import io.cloudslang.content.xml.entities.inputs.CommonInputs;
import io.cloudslang.content.xml.entities.inputs.CustomInputs;
import io.cloudslang.content.xml.entities.Constants;
import io.cloudslang.content.xml.utils.ResultUtils;
import io.cloudslang.content.xml.utils.XmlUtils;
import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.namespace.NamespaceContext;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by markowis on 03/03/2016.
 */
public class RemoveService {
    public Map<String, String> execute(CommonInputs commonInputs, CustomInputs customInputs){
        Map<String, String> result = new HashMap<>();

        try {
            Document doc = XmlUtils.getDocument(commonInputs);
            NamespaceContext context = XmlUtils.getNamespaceContext(commonInputs, doc);
            NodeList nodeList = XmlUtils.evaluateXPathQuery(doc, context, commonInputs.getXPathQuery());

            XmlUtils.validateNodeList(nodeList);

            removeFromNodeList(nodeList, customInputs.getAttributeName());
            ResultUtils.populateValueResult(result, Constants.SUCCESS, Constants.SuccessMessages.REMOVE_SUCCESS,
                    XmlUtils.nodeToString(doc), Constants.ReturnCodes.SUCCESS);

        } catch (XPathExpressionException e) {
            ResultUtils.populateValueResult(result, Constants.FAILURE,
                    Constants.ErrorMessages.XPATH_PARSING_ERROR + e.getMessage(), Constants.EMPTY_STRING, Constants.ReturnCodes.FAILURE);
        } catch (TransformerException te) {
            ResultUtils.populateValueResult(result, Constants.FAILURE,
                    Constants.ErrorMessages.TRANSFORMER_ERROR + te.getMessage(), Constants.EMPTY_STRING, Constants.ReturnCodes.FAILURE);
        } catch (Exception e) {
            ResultUtils.populateValueResult(result, Constants.FAILURE,
                    Constants.ErrorMessages.PARSING_ERROR + e.getMessage(), Constants.EMPTY_STRING, Constants.ReturnCodes.FAILURE);
        }

        return result;
    }

    private static void removeFromNodeList(NodeList nodeList, String attributeName) throws Exception{
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);

            if(node.getNodeType() != Node.ELEMENT_NODE){
                throw new Exception(Constants.ErrorMessages.REMOVE_FAILURE + Constants.ErrorMessages.NEED_ELEMENT_TYPE);
            }

            if (StringUtils.isBlank(attributeName)) {
                node.getParentNode().removeChild(node);
            }
            else{
                node.getAttributes().removeNamedItem(attributeName);
            }
        }
    }
}
