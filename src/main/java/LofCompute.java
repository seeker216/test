import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class LofCompute {
    public static int INT_K=5;
    public LofCompute(List<DataNode> allNodes){
        getKdAndKn(allNodes);
        calReachDis(allNodes);
        calReachDensity(allNodes);
        calLof(allNodes);
    }

    private void calLof(List<DataNode> kdAndKnList) {
        for (DataNode node : kdAndKnList) {
            List<DataNode> tempNodes = node.getkNeighbor();
            double sum = 0.0;
            for (DataNode tempNode : tempNodes) {
                double rd = getRD(tempNode.getNodeName(), kdAndKnList);
                sum = rd / node.getReachDensity() + sum;
            }
            sum = sum / (double) INT_K;
            node.setLof(sum);
        }
    }

    private void calReachDensity(List<DataNode> kdAndKnList) {
        for (DataNode node : kdAndKnList) {
            List<DataNode> tempNodes = node.getkNeighbor();
            double sum = 0.0;
            double rd = 0.0;
            for (DataNode tempNode : tempNodes) {
                sum = tempNode.getReachDis() + sum;
            }
            rd = (double) INT_K / sum;
            node.setReachDensity(rd);
        }

    }

    private double getRD(String nodeName, List<DataNode> nodeList) {
        double kDis = 0;
        for (DataNode node : nodeList) {
            if (nodeName.trim().equals(node.getNodeName().trim())) {
                kDis = node.getReachDensity();
                break;
            }
        }
        return kDis;

    }

    private double getKDis(String nodeName, List<DataNode> nodeList) {
        double kDis = 0;
        for (DataNode node : nodeList) {
            if (nodeName.trim().equals(node.getNodeName().trim())) {
                kDis = node.getkDistance();
                break;
            }
        }
        return kDis;

    }

    private void calReachDis(List<DataNode> kdAndKnList) {
        for (DataNode node : kdAndKnList) {
            List<DataNode> tempNodes = node.getkNeighbor();
            for (DataNode tempNode : tempNodes) {
                //获取tempNode点的k-距离
                double kDis = getKDis(tempNode.getNodeName(), kdAndKnList);
                //reachdis(p,o)=max{ k-distance(o),d(p,o)}
                if (kDis < tempNode.getDistance()) {
                    tempNode.setReachDis(tempNode.getDistance());
                } else {
                    tempNode.setReachDis(kDis);
                }
            }
        }
    }

    private void getKdAndKn(List<DataNode> allNodes) {
//        List<DataNode> kdAndKnList = new ArrayList<DataNode>();
        for (int i = 0; i < allNodes.size(); i++) {
            List<DataNode> tempNodeList = new ArrayList<DataNode>();
            DataNode nodeA = new DataNode(allNodes.get(i).getNodeName(), allNodes.get(i).getDimensioin());
            //1,找到给定点NodeA与其他点NodeB的距离，并记录在NodeB点的distance变量中。
            for (int j = 0; j < allNodes.size(); j++) {
                DataNode nodeB = new DataNode(allNodes.get(j).getNodeName(), allNodes.get(j).getDimensioin());
                //计算NodeA与NodeB的距离(distance)
                double tempDis = getDis(nodeA, nodeB);
                nodeB.setDistance(tempDis);
                tempNodeList.add(nodeB);
            }

            //2,对所有NodeB点中的距离（distance）进行升序排序。
            Collections.sort(tempNodeList, new DistComparator());
            for (int k = 1; k < INT_K; k++) {
                //3,找到NodeB点的前5位的欧几里得距离点，并记录到到NodeA的kNeighbor变量中。
//                nodeA.getkNeighbor().add(tempNodeList.get(k));
                String nodeName=tempNodeList.get(k).getNodeName();
                for (DataNode dn:allNodes){
                    if(dn.getNodeName().equals(nodeName)){
                        allNodes.get(i).getkNeighbor().add(dn);
                    }
                }
                if (k == INT_K - 1) {
                    //4,找到NodeB点的第5位距离，并记录到NodeA点的kDistance变量中。
                    allNodes.get(i).setkDistance(tempNodeList.get(k).getDistance());
                }
            }
//            kdAndKnList.add(nodeA);
        }

//        return kdAndKnList;
    }

    /*
    给定两个元组  计算元组间距离
     */
    private double getDis(DataNode nodeA, DataNode nodeB) {
        double dis = 0.0;
        double[] dimA = nodeA.getDimensioin();
        double[] dimB = nodeB.getDimensioin();
        if (dimA.length == dimB.length) {
            for (int i = 0; i < dimA.length; i++) {
                double temp = Math.pow(dimA[i] - dimB[i], 2);
                dis = dis + temp;
            }
            dis = Math.pow(dis, 0.5);
        }
        return dis;
    }

    class DistComparator implements Comparator<DataNode> {
        public int compare(DataNode A, DataNode B) {
            //return A.getDistance() - B.getDistance() < 0 ? -1 : 1;
            if((A.getDistance()-B.getDistance())<0)
                return -1;
            else if((A.getDistance()-B.getDistance())>0)
                return 1;
            else return 0;
        }
    }

}
