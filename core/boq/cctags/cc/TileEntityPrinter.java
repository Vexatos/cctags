package boq.cctags.cc;

import static boq.utils.misc.Utils.wrap;
import net.minecraft.item.ItemStack;
import boq.cctags.tag.TagData;

import com.google.common.collect.ObjectArrays;

import dan200.computer.api.IComputerAccess;

public class TileEntityPrinter extends TileEntityPeripheral<PrinterData> {

    public TileEntityPrinter() {
        super(new PrinterData());
        helper = new PrinterHelper(data);
    }

    private final PrinterHelper helper;

    private final static String[] printerMethods = { "inkLevel", "print" };

    private final static String[] methods = ObjectArrays.concat(commonMethods, printerMethods, String.class);

    private final static int ownMethodStart = commonMethods.length;

    @Override
    public String[] getMethodNames() {
        return methods;
    }

    @Override
    public Object[] callMethod(IComputerAccess computer, int method, Object[] arguments) throws Exception {
        if (method < ownMethodStart)
            return super.callMethod(computer, method, arguments);

        if (method == ownMethodStart + 0) // intLevel
            return wrap(data.inkLevel);
        else if (method == ownMethodStart + 1) {// print
            TagData data = readData();

            if (data == null)
                return wrap(false, "No tag");

            String icon = arguments[0].toString();
            String label = (arguments.length > 1) ? arguments[1].toString() : null;

            Object[] result = helper.printTag(data, icon, label);

            if (result[0].equals(true))
                writeData(data);

            return result;
        }

        throw new IllegalArgumentException("Unknown method: " + method);
    }

    public boolean addInk(ItemStack stack) {
        if (helper.addInk(stack)) {
            onInventoryChanged();
            return true;
        }

        return false;
    }

}
