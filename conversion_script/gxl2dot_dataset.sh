#Script to convert GXL graphs from http://homepages.engineering.auckland.ac.nz/~parallel/OptimalTaskScheduling/OptimalSchedules.html
#into input and output .dot files
#dependencies requires gxl2dot

#how to run send optimal gxl graphs as arguments
#the input graph will be placed in the input folder
#the output graph will be placed in the output folder

OUTPUT_DIR="$1"
shift
for FILE_NAME in "$@"
do
    RESULT_NAME="`basename $FILE_NAME .gxl`.dot"
    gxl2dot $FILE_NAME -o $RESULT_NAME

    #remove graph infomation from file
    sed -i -E ':a;N;$!ba;s/\tgraph \[(.)*(\n)?(.)*\n\t\];\n//g' $RESULT_NAME

    #put parameters on the same line
    sed -i ':a;N;$!ba;s/,\n\t\t/,/g' $RESULT_NAME

    #change the naming of the start time parameter to match requirments
    sed -i 's/"Start time"/Start/g' $RESULT_NAME

    #remove finish time parameter
    sed -i -E 's/"Finish time"=([0-9]*),//g' $RESULT_NAME

    #copy the output file into the output folder
    cp $RESULT_NAME $OUTPUT_DIR/output/$RESULT_NAME

    #remove the start time parameter from the input graph
    sed -i -E 's/Start=([0-9]*),//g' $RESULT_NAME

    #remove the processor parameter from the input graph
    sed -i -E 's/Processor=([0-9]*),//g' $RESULT_NAME

    mv $RESULT_NAME $OUTPUT_DIR/input/$RESULT_NAME

done
    
